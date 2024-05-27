/**
 * Copyright 2021-2024 Dimitris Psathas <dimitrisinbox@gmail.com>
 * <p>
 * This file is part of DiapasonFX.
 * <p>
 * DiapasonFX is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License  as  published by  the  Free Software
 * Foundation,  either version 3 of the License,  or (at your option)  any later
 * version.
 * <p>
 * DiapasonFX is distributed in the hope that it will be useful,  but  WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE.  See the  GNU General Public License  for more details.
 * <p>
 * You should have received a copy of the  GNU General Public License along with
 * DiapasonFX. If not, see <http://www.gnu.org/licenses/>.
 */


package com.dimitris47.diapasonfx;

import java.util.ArrayList;

public class Frequency {
    static double[] defFreq = {261.626, 277.183, 293.665, 311.127, 329.626, 349.228,
            369.994, 391.995, 415.305, 440.000, 466.164, 493.883};
    ArrayList<Double> currFreq = new ArrayList<>(12);

    public Frequency(int pitch) {
        for (var item : defFreq) {
            assert false;
            currFreq.add(item * pitch / 440);
        }
    }
}
