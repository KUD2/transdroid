/*
 *	This file is part of Transdroid <http://www.transdroid.org>
 *
 *	Transdroid is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	Transdroid is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with Transdroid.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.transdroid.daemon.task;

import android.os.Bundle;

import org.transdroid.daemon.DaemonMethod;
import org.transdroid.daemon.IDaemonAdapter;
import org.transdroid.daemon.Torrent;

import java.util.ArrayList;
import java.util.List;

public class SetTrackersTask extends DaemonTask {
    protected SetTrackersTask(IDaemonAdapter adapter, Torrent targetTorrent, Bundle data) {
        super(adapter, DaemonMethod.SetTrackers, targetTorrent, data);
    }

    public static SetTrackersTask create(IDaemonAdapter adapter, Torrent targetTorrent, List<String> list) {
        Bundle data = new Bundle();
        data.putStringArrayList("NEW_TRACKERS_LSIT", new ArrayList<>(list));
        return new SetTrackersTask(adapter, targetTorrent, data);
    }

    public ArrayList<String> getNewTrackers() {
        return extras.getStringArrayList("NEW_TRACKERS_LSIT");
    }
}
