package org.firstinspires.ftc.teamcode.teleops.tuning;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.mechanisms.Flywheel;

@TeleOp
public class kPTuner extends OpMode {

    Flywheel flywheel = new Flywheel();
    double[] increments = {0.0000001,0.000001, 0.00001, 0.0001, 0.001, 0.01};
    int incrementIdx = 4;
    public double kV = 0.000121; // valor mais próximo que permite o motor à chegar em 3600 rpm
    public double kS = 0.27; // potência mínima para mexer o motor
    public double kP = 0.000003; // Precisa ser calibrado melhor
    public double goalRPM = 3600; // 3600 rotações por minuto permite lançamentos à longas distâncias mais facilmente

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
        }
        else if (gamepad1.b) {
            goalRPM = 3600;
        }


        double currentStep = increments[incrementIdx];
        if (gamepad1.dpadUpWasPressed()) {kP += currentStep;}
        if (gamepad1.dpadDownWasPressed()) {kP -= currentStep;}

        if (gamepad1.right_bumper){kV += currentStep;}
        if (gamepad1.left_bumper){kV -= currentStep;}

        double feedForward = (kV * goalRPM) + kS;
        double error = goalRPM - currentRPM;
        double feedBack = error * kP;

        double power = feedForward - feedBack;

        power = Math.max(-1.0, Math.min(1.0, power));

        flywheel.setMotorPower(power);

        telemetry.addData("Step", "%.6f", currentStep);

        telemetry.addData("kP", "%.6f", kP);
        telemetry.addData("kV", "%.6f", kV);
        telemetry.addData("kS", "%.4f", kS);

        telemetry.addData("RPM atual", "%.2f RPM", currentRPM);
        telemetry.addData("Ticks per Sec", "%.2f", ticksPerSecond);
        telemetry.addData("Goal RPM", "%.2f", goalRPM);

        // Teste dos motores
        telemetry.addData("Power M1", "%.3f", flywheel.m1.getPower());
        telemetry.addData("Power M2", "%.3f", flywheel.m2.getPower());

        telemetry.addData("M1 velocity", flywheel.m1.getVelocity());
        telemetry.addData("M2 velocity", flywheel.m2.getVelocity());

        telemetry.addData("M1 position", flywheel.m1.getCurrentPosition());
        telemetry.addData("M2 position", flywheel.m2.getCurrentPosition());
        //

        // Medir a corrente dos motores e exibir um aviso no driverHub caso ultrapasse 3 amps
        telemetry.addData("Corrente M1", flywheel.m1.getCurrent(CurrentUnit.AMPS));
        telemetry.addData("Corrente M2", flywheel.m2.getCurrent(CurrentUnit.AMPS));

        if (flywheel.m1.getCurrent(CurrentUnit.AMPS) > 3){
            telemetry.addLine("ALERTA! CORRENTE ALTA!");
        }

        telemetry.addData("Error", error);
        telemetry.update();

    }

}
