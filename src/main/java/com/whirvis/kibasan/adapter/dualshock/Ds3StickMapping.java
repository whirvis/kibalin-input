package com.whirvis.kibasan.adapter.dualshock;

import com.whirvis.kibasan.adapter.AnalogMapping;
import com.whirvis.kibasan.feature.AnalogStick;

public class Ds3StickMapping
		extends AnalogMapping<AnalogStick> {

	public final int byteOffsetX;
	public final int byteOffsetY;
	
	public Ds3StickMapping(AnalogStick stick, int byteOffsetX, int byteOffsetY) {
		super(stick);
		this.byteOffsetX = byteOffsetX;
		this.byteOffsetY = byteOffsetY;
	}

}