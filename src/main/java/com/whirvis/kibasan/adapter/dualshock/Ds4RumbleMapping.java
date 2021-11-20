package com.whirvis.kibasan.adapter.dualshock;

import com.whirvis.kibasan.adapter.RumbleMapping;
import com.whirvis.kibasan.feature.RumbleMotor;

public class Ds4RumbleMapping extends RumbleMapping {

	public final int byteOffset;

	public Ds4RumbleMapping(RumbleMotor motor, int byteOffset) {
		super(motor);
		this.byteOffset = byteOffset;
	}

}