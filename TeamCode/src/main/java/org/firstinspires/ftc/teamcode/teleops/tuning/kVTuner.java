package org.firstinspires.ftc.teamcode.teleops.tuning;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.mechanisms.Flywheel;

@TeleOp
public class kVTuner extends OpMode {

    Flywheel flywheel = new Flywheel();
    double[] increments = {0.000001, 0.00001, 0.0001, 0.001, 0.01};
    int incrementIdx = 4;
    public double kV = 0.000121;
    public double kS = 0.27; //REGULAR kS primeiro
    public double goalRPM = 3600;

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
            goalRPM = 3680;
        }

        double currentStep = increments[incrementIdx];

        if (gamepad1.dpadUpWasPressed()) {
            kV += currentStep;
        }

        if (gamepad1.dpadDownWasPressed()) {
            kV -= currentStep;
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

        telemetry.addData("M1 velocity", flywheel.m1.getVelocity());
        telemetry.addData("M2 velocity", flywheel.m2.getVelocity());

        telemetry.addData("M1 position", flywheel.m1.getCurrentPosition());
        telemetry.addData("M2 position", flywheel.m2.getCurrentPosition());

        telemetry.addData("Corrente M1", flywheel.m1.getCurrent(CurrentUnit.AMPS));
        telemetry.addData("Corrente M2", flywheel.m2.getCurrent(CurrentUnit.AMPS));

        if (flywheel.m1.getCurrent(CurrentUnit.AMPS) > 3){
            telemetry.addLine("ALERTA! CORRENTE ALTA!");
        }


        telemetry.update();
    }
}
