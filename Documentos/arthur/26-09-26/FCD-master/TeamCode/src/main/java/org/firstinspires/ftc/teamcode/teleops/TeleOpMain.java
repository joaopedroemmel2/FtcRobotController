package org.firstinspires.ftc.teamcode.teleops;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.teamcode.mechanisms.Limelight;
import org.firstinspires.ftc.teamcode.mechanisms.MecanumDrive;
import org.firstinspires.ftc.teamcode.mechanisms.Turret;

@TeleOp
public class TeleOpMain extends OpMode {

    //instanciando o seu mecanismo isolado
    private MecanumDrive drive;
    Limelight limelight;
    Turret turret;

    // configuracoes de pilotagem
    private static final double DEADZONE = 0.05;
    private int movementInverter = 1;
    private boolean isFieldCentric = true;
    private boolean lastBackState = false;

    @Override
    public void init() {
        drive = new MecanumDrive();
        drive.init(hardwareMap);

        limelight = new Limelight();
        limelight.init(hardwareMap);

        turret = new Turret();
        turret.init(hardwareMap);

        telemetry.addLine("MecanumDrive Inicializado.");
        telemetry.addLine("Odometria Pinpoint pronta.");
        telemetry.addLine("Limelight3A Iniciada");

        telemetry.update();
    }


    private double driveSpeed = 1.0;
    private boolean lastDpadUp = false;
    private boolean lastDpadDown = false;

    @Override
    public void loop() {
        //inversao de movimento
        if (gamepad1.dpad_left) movementInverter *= -1;

        //alternar entre Field Centric e Robot Centric (gatilho da direita)
        if (gamepad1.back && !lastBackState) {
            isFieldCentric = !isFieldCentric;
        }
        lastBackState = gamepad1.back;

        // aplicação de zona morta (elimina arrasto quando solta o analogico)
        double rawX = gamepad1.left_stick_x;
        double rawY = gamepad1.left_stick_y;
        double rawTurn = gamepad1.right_stick_x;

        double x = (Math.abs(rawX) > DEADZONE) ? rawX : 0.0;
        double y = (Math.abs(rawY) > DEADZONE) ? rawY : 0.0;
        double turn = (Math.abs(rawTurn) > DEADZONE) ? rawTurn : 0.0;

        // suavização cubica e Inversão
        //ele eleva ao cubo para dar movimentos finos quando o analogico é pouco pressionado, mas mantem a velocidade maxima no fim do curso
        x = Math.pow(x, 3) * movementInverter;
        y = Math.pow(y, 3) * movementInverter;
        turn = Math.pow(turn, 3);


        x *= driveSpeed;
        y *= driveSpeed;
        turn *= driveSpeed;

        // Controle da potência máxima pelo D-Pad
        if (gamepad1.dpad_up && !lastDpadUp) {
            driveSpeed = Math.min(1.0, driveSpeed + 0.30);
        }

        if (gamepad1.dpad_down && !lastDpadDown) {
            driveSpeed = Math.max(0.10, driveSpeed - 0.30);
        }

        lastDpadUp = gamepad1.dpad_up;
        lastDpadDown = gamepad1.dpad_down;

        // envio dos comandos para o mecanismo (O botao Start reseta o norte do IMU/pinpoint)
        if (isFieldCentric) {
            drive.moveFieldCentric(x, y, turn, gamepad1.start);
        } else {
            drive.moveDriveTrain(x, y, turn);
        }

        // Limelight //
        YawPitchRollAngles orientation = limelight.imu.getRobotYawPitchRollAngles();
        limelight.limelight.updateRobotOrientation(orientation.getYaw());

        telemetry.addData("--- PILOTAGEM ---", "");
        telemetry.addData("Modo", isFieldCentric ? "FIELD CENTRIC" : "ROBOT CENTRIC");
        telemetry.addData("Direção", movementInverter == 1 ? "Normal" : "Invertida");
        telemetry.addData("Eixos", "X: %.2f | Y: %.2f | Giro: %.2f", x, y, turn);

        telemetry.addData("--- DIAGNÓSTICO DO HARDWARE ---", "");
        telemetry.addData("Correntes (A)", drive.getCurrents());
        telemetry.addData("Potências", drive.getPowers());
        telemetry.addData("Encoders", drive.getPositions());

        telemetry.addLine("---------------------Limelight--------------------");
        LLResult llresult = limelight.limelight.getLatestResult();
        turret.update(llresult);
        if (llresult != null && llresult.isValid()){
            Pose3D botPose = llresult.getBotpose_MT2();
            telemetry.addData("Tx", llresult.getTx());
            telemetry.addData("Ty", llresult.getTy());
            telemetry.addData("Ta", llresult.getTa());

        telemetry.update();

    }
        telemetry.addLine("--- TURRET / LIMELIGHT ---");
        telemetry.addData("Alvo válido", llresult != null && llresult.isValid());
        if (llresult != null && llresult.isValid()) {
            telemetry.addData("Tx (erro de mira)", llresult.getTx());
        }
}}