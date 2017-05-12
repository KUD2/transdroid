package org.transdroid.connect.clients.rtorrent

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Before
import org.junit.Test
import org.transdroid.connect.Configuration
import org.transdroid.connect.clients.Client
import org.transdroid.connect.mock.MockTorrent
import org.transdroid.connect.model.Priority

class RtorrentMockTest {

    private lateinit var server: MockWebServer
    private lateinit var rtorrent: Rtorrent

    @Before
    fun setUp() {
        server = MockWebServer()
        rtorrent = Rtorrent(Configuration(Client.RTORRENT, server.url("/").toString(), "/RPC2"))
    }

    @Test
    fun clientVersion() {
        server.enqueue(mock("<string>0.9.6</string>"))
        rtorrent.clientVersion()
                .test()
                .assertValue("0.9.6")
        server.takeRequest()
    }

    @Test
    fun torrents() {
        server.enqueue(mock("<array><data><value><array><data><value><string>59066769B9AD42DA2E508611C33D7C4480B3857B</string></value><value><string>ubuntu-17.04-desktop-amd64.iso</string></value><value><i8>0</i8></value><value><i8>0</i8></value><value><i8>0</i8></value><value><i8>0</i8></value><value><i8>0</i8></value><value><i8>0</i8></value><value><i8>0</i8></value><value><i8>1609039872</i8></value><value><i8>1609039872</i8></value><value><i8>1492077159</i8></value><value><i8>0</i8></value><value><i8>0</i8></value><value><i8>0</i8></value><value><string></string></value><value><string></string></value><value><string></string></value><value><string></string></value><value><string></string></value><value><string></string></value><value><i8>0</i8></value><value><i8>0</i8></value></data></array></value></data></array>"))
        rtorrent.torrents()
                .test()
                .assertValue { it.hash == "59066769B9AD42DA2E508611C33D7C4480B3857B" }
        server.takeRequest()
    }

    @Test
    fun files() {
        server.enqueue(mock("<array><data><value><array><data><value><string>ubuntu-17.04-desktop-amd64.iso</string></value><value><i8>1609039872</i8></value><value><i8>0</i8></value><value><i8>3069</i8></value><value><i8>1</i8></value><value><string>/downloads/ubuntu-17.04-desktop-amd64.iso</string></value></data></array></value></data></array>"))
        rtorrent.files(MockTorrent.downloading)
                .test()
                .assertValue {
                    it.key == "0" &&
                            it.name == "ubuntu-17.04-desktop-amd64.iso" &&
                            it.relativePath == "ubuntu-17.04-desktop-amd64.iso" &&
                            it.fullPath == "ubuntu-17.04-desktop-amd64.iso" &&
                            it.totalSize == 1609039872L &&
                            it.downloaded == 0L &&
                            it.priority == Priority.NORMAL
                }
        server.takeRequest()
    }

    @Test
    fun details() {
        server.enqueue(mock("<array><data><value><array><data><value><string>http://torrent.ubuntu.com:6969/announce</string></value></data></array></value><value><array><data><value><string>http://ipv6.torrent.ubuntu.com:6969/announce</string></value></data></array></value></data></array>"))
        rtorrent.details(MockTorrent.error)
                .test()
                .assertValue {
                    it.trackers.size == 2 &&
                            it.trackers[0] == "http://torrent.ubuntu.com:6969/announce" &&
                            it.trackers[1] == "http://ipv6.torrent.ubuntu.com:6969/announce" &&
                            it.errors.size == 1 &&
                            it.errors[0] == "tracker error"
                }
        server.takeRequest()
    }

    @Test
    fun addByUrl() {
        server.enqueue(mock("<string>0.9.6</string>"))
        server.enqueue(mock("<i4>0</i4>"))
        rtorrent.addByUrl(MockTorrent.torrentUrl)
                .test()
                .assertNoErrors()
        server.takeRequest()
        server.takeRequest()
    }

    @Test
    fun addByMagnet() {
        server.enqueue(mock("<string>0.9.6</string>"))
        server.enqueue(mock("<i4>0</i4>"))
        rtorrent.addByMagnet(MockTorrent.magnetUrl)
                .test()
                .assertNoErrors()
        server.takeRequest()
        server.takeRequest()
    }

    @Test
    fun addByFile() {
        server.enqueue(mock("<string>0.9.6</string>"))
        server.enqueue(mock("<i4>0</i4>"))
        server.enqueue(mock("<i4>0</i4>"))
        rtorrent.addByFile(MockTorrent.torrentFile)
                .test()
                .assertNoErrors()
        server.takeRequest()
        server.takeRequest()
        server.takeRequest()
    }

    @Test
    fun start() {
        server.enqueue(mock("<i4>0</i4>"))
        server.enqueue(mock("<i4>0</i4>"))
        rtorrent.start(MockTorrent.downloading)
                .test()
                .assertValue { it.canStop }
        //server.takeRequest()
        //server.takeRequest()
    }

    @Test
    fun stop() {
        server.enqueue(mock("<i4>0</i4>"))
        server.enqueue(mock("<i4>0</i4>"))
        rtorrent.stop(MockTorrent.seeding)
                .test()
                .assertValue { it.canStart }
        //server.takeRequest()
        //server.takeRequest()
    }

    @Test
    fun resume() {
        server.enqueue(mock("<i4>0</i4>"))
        rtorrent.resume(MockTorrent.downloading)
                .test()
                .assertValue { it.canPause }
        server.takeRequest()
    }

    @Test
    fun pause() {
        server.enqueue(mock("<i4>0</i4>"))
        rtorrent.pause(MockTorrent.seeding)
                .test()
                .assertValue { it.canResume }
        server.takeRequest()
    }

    private fun mock(params: String): MockResponse? {
        return MockResponse()
                .addHeader("Content-Type", "application/xml; charset=UTF-8")
                .setBody("<?xml version=\"1.0\"?>\n" +
                        "<methodResponse>\n" +
                        "  <params>\n" +
                        "    <param><value>{$params}</value></param>\n" +
                        "  </params>\n" +
                        "</methodResponse>")
    }

}