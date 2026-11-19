package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Ramp {
    CRServo LSRamp;
    CRServo RSRamp;

    public void init(HardwareMap hwmap){
        LSRamp = hwmap.get(CRServo.class, "LSRamp");
        RSRamp = hwmap.get(CRServo.class, "RSRamp");

        LSRamp.setDirection(DcMotorSimple.Direction.FORWARD);
        RSRamp.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void setPower(double power){
        LSRamp.setPower(power);
        RSRamp.setPower(power);
    }
}
