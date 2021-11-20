package com.whirvis.kibasan.adapter.dualshock;

import com.whirvis.kibasan.adapter.AnalogMapping;
import com.whirvis.kibasan.feature.AnalogStick;

public class Ds4StickMapping
		extends AnalogMapping<AnalogStick> {

	public final int byteOffsetX;
	public final int byteOffsetY;
	
	public Ds4StickMapping(AnalogStick stick, int byteOffsetX, int byteOffsetY) {
		super(stick);
		this.byteOffsetX = byteOffsetX;
		this.byteOffsetY = byteOffsetY;
	}

}