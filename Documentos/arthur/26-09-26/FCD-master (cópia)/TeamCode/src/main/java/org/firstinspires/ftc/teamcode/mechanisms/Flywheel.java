package org.firstinspires.ftc.teamcode.mechanisms;


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

public class Flywheel {

    public DcMotorEx m1;
    public DcMotorEx m2;

    private static final double TICKS_PER_REV = 28.0;

    private double kS = 0.27, kV=0.000121, kP=0.000003;

    public void init(HardwareMap hwmap) {
        m1 = hwmap.get(DcMotorEx.class, "leftFlywheel");
        m2 = hwmap.get(DcMotorEx.class, "rightFlywheel");

        m1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        m2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        m1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        m2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);


        m1.setDirection(DcMotorSimple.Direction.FORWARD);
        m2.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void setMotorPower(double power) {
        m1.setPower(power);
        m2.setPower(power);
    }

    public void stopMotors() {
        m1.setPower(0);
        m2.setPower(0);
    }

    public void setMotorRPM(double targetRPM) {
        //esse comando tem que estar em todos os loops
        double error = targetRPM - getRPM();
        double ff = (kV * targetRPM) + kS;
        double fb = error * kP;
        double power = ff + fb;

        setMotorPower(power);
    }

    public double getTicksPerSecond() {
        return m1.getVelocity();
    }

    public double getRPM() {
        return (getTicksPerSecond() / TICKS_PER_REV) * 60.0;
    }
}
