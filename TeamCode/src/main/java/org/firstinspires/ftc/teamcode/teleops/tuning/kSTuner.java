package org.firstinspires.ftc.teamcode.teleops.tuning;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.mechanisms.Flywheel;

@TeleOp
public class kSTuner extends OpMode {

    Flywheel flywheel = new Flywheel();
    double[] increments = {0.000001, 0.00001, 0.0001, 0.001, 0.01};
    int incrementIdx = 4;
    public double kV = 0.01;
    public double kS = 0.27; //REGULAR kS primeiro
    public double goalRPM = 1500;

    @Override
    public void init() {
        flywheel.init(hardwareMap);
    }

    @Override
    public void loop() {

        double ticksPerSecond = flywheel.getTicksPerSecond();
        double currentRPM = flywheel.getRPM();

        if (gamepad1.dpadRightWasPressed() && incrementIdx < 4) {
            incrementIdx++;
        } else if (gamepad1.dpadLeftWasPressed() && incrementIdx > 0) {
            incrementIdx--;
        }

        if (gamepad1.a) {
            goalRPM = 1500;
        } else if (gamepad1.b) {
            goalRPM = 800;
        }

        double currentStep = increments[incrementIdx];

        if (gamepad1.dpadUpWasPressed()) {
            kS += currentStep;
        }

        if (gamepad1.dpadDownWasPressed()) {
            kS -= currentStep;
        }

        double power = (kV * goalRPM) + kS;

        flywheel.setMotorPower(power);

        telemetry.addData("Step", "%.6f", currentStep);
        telemetry.addData("kV", "%.6f", kV);
        telemetry.addData("kS", "%.4f", kS);

        telemetry.addData("RPM atual", "%.2f RPM", currentRPM);
        telemetry.addData("Ticks per Sec", "%.2f", ticksPerSecond);
        telemetry.addData("Goal RPM", "%.2f", goalRPM);

        telemetry.addData("Power M1", "%.3f", flywheel.m1.getPower());
        telemetry.addData("Power M2", "%.3f", flywheel.m2.getPower());

        telemetry.update();
    }
}
