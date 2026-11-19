package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

public class Intake {
    DcMotorEx CoreHex1;

    public void init(HardwareMap hwmap){
        CoreHex1 = hwmap.get(DcMotorEx.class, "corehex1");

        CoreHex1.setDirection(DcMotorSimple.Direction.FORWARD);
    }
    public void setPower(double power){
        CoreHex1.setPower(power);
    }
    public double getCurrent(){
       return CoreHex1.getCurrent(CurrentUnit.AMPS);
    }
}
