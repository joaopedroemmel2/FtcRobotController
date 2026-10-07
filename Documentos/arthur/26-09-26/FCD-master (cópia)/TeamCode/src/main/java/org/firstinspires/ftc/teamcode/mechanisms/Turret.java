package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.robotcore.util.Range;


public class Turret {
    private CRServo turretL;
    private CRServo turretR;

    private double kP = 0.0001;
    private double kD = 0.0000;
    private double goalX = 0;
    private double lastError = 0;
    private double angleTolerance = 0.2;
    private final double MAX_POWER = 0.6;
    private double power = 0;
    private final ElapsedTime timer = new ElapsedTime();


    public void init(HardwareMap hwmap) {
        turretL = hwmap.get(CRServo.class, "turretL");
        turretR = hwmap.get(CRServo.class, "turretR");

    }

    public void setkP(double newkP) {
        this.kP = newkP;
    }
    public double getkP() {
        return kP;
    }

    public double getkD() {
        return kD;
    }

    public void setkD(double newkD) {
        this.kD = newkD;
    }

    public void resetTimer() {
        timer.reset();
    }

    public void update(LLResult curID) {
        double deltaTime = timer.seconds();
        timer.reset();

        if (curID == null) {
            turretL.setPower(0);
            turretR.setPower(0);
            lastError = 0;
            return;
        }

        double error = goalX - curID.getTx();
        double pTerm = error * kP;

        double dTerm = 0;
        if (deltaTime > 0) {
            dTerm = ((error - lastError) / deltaTime) * kD;
        }

        if (Math.abs(error) < angleTolerance) {
            power = 0;
        } else {
            power = Range.clip(pTerm + dTerm, -MAX_POWER, MAX_POWER);
        }

        turretL.setPower(power);
        turretR.setPower(power);
        lastError = error;
    }
}
