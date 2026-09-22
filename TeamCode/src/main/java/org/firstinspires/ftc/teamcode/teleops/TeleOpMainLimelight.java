/* package org.firstinspires.ftc.teamcode;

import static java.lang.Thread.sleep;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.teamcode.mechanisms.InTake;
import org.firstinspires.ftc.teamcode.mechanisms.MecanumDrive;
import org.firstinspires.ftc.teamcode.mechanisms.OutTake;
import org.firstinspires.ftc.teamcode.mechanisms.TwoMotorsDrive;
import org.firstinspires.ftc.teamcode.mechanisms.WebCam;
import org.firstinspires.ftc.teamcode.mechanisms.Limelight;

@TeleOp
public class TeleOpMain extends OpMode {
    TwoMotorsDrive drive;
    InTake inTake;

    OutTake outTake;

    WebCam cam;

    MecanumDrive mecanumDrive;

    Limelight limelight;

    boolean lastDpadUp;
    boolean lastDpadDown;

    boolean currentDpadUp;

    boolean currentDpadDown;
    double flywheelPower;
    double coreHexPower;
    double coreHex2Power;
    boolean isOuttakeLocked = false;
    double lockedOuttakePower = 0;
    int direction;
    int coreHexDirection;

    @Override
    public void init(){
        lastDpadDown = false;
        lastDpadUp = false;

        mecanumDrive = new MecanumDrive();
        mecanumDrive.init(hardwareMap);

        inTake = new InTake();
        inTake.init(hardwareMap);

        outTake = new OutTake();
        outTake.init(hardwareMap);

        cam = new WebCam();
        cam.init(hardwareMap);

        int direction = 0;
        coreHexDirection = 1;

        limelight = new Limelight();
        limelight.init(hardwareMap);
    }

    @Override
    public void loop(){
        //drive.arcadeDrive(gamepad1.left_stick_y * direction * 0.6, gamepad1.left_stick_x*0.6);
        mecanumDrive.moveDriveTrain(gamepad1.left_stick_x, gamepad1.left_stick_y, gamepad1.right_stick_x);
        inTake.setCoreHexPowers(coreHexPower, -coreHex2Power);
        dpadControl();
        outTakeTriggerControl();
        coreHexBumperControl();
        defaultPowerTriggerControl();

        
        YawPitchRollAngles orientation = limelight.imu.getRobotYawPitchRollAngles();
        limelight.limelight.updateRobotOrientation(orientation.getYaw());



        if (gamepad1.a) {
            autoShoot();

        }


        //Telemetria

        //Movimento
        telemetry.addData("Corrente dos motores de movimento(EsquerdoFront/DireitoFront/EsquerdoBack/DireitoBack)", mecanumDrive.getCurrents());

        //Out-take
        telemetry.addLine("-----------------------Out-take---------------------");
        telemetry.addData("Potência", outTake.getPower());
        telemetry.addData("Velocidade", outTake.getVelocity());
        telemetry.addData("Corrente", outTake.getCurrent());

        //Webcam
        telemetry.addLine("------------------------WebCam----------------------");
        telemetry.addData("Tags detectadas", cam.getDetectionsNumber());
        telemetry.addData("ID da Tag", cam.getTagId());
        telemetry.addData("Distância", cam.getTagDistanceCentimeters());
        telemetry.addData("Ângulo de ajuste", cam.getAimAngle());

        //In-take
        telemetry.addData("Orientação do coreHex", coreHexDirection == 1 ? "Engolir" : "Repelir");

        //Limelight
        telemetry.addLine("---------------------Limelight--------------------");
        LLResult llresult = limelight.limelight.getLatestResult();
        if (llresult != null && llresult.isValid()){
            Pose3D botPose = llresult.getBotpose_MT2();
            telemetry.addData("Tx", llresult.getTx());
            telemetry.addData("Ty", llresult.getTy());
            telemetry.addData("Ta", llresult.getTa());
        }
    }

    public void dpadControl(){
        currentDpadUp = gamepad1.dpad_up;
        currentDpadDown = gamepad1.dpad_down;

        if (currentDpadUp && !lastDpadUp){
            flywheelPower += 0.01;
        }
        lastDpadUp = currentDpadUp;

        if (currentDpadDown && !lastDpadDown){
            flywheelPower -= 0.01;
        }
        lastDpadDown = currentDpadDown;
        outTake.setPower(flywheelPower);
    }
    public void autoShoot(){
        double flywheelVelocity = 0;
        double distance = cam.getTagDistanceCentimeters();

        while (cam.getTagId() == 20){
            telemetry.addLine("Calculando velocidade...");
            flywheelVelocity = Math.pow(distance, 1.16) + distance + 780 + 45*Math.sin(((double) 1 /13)*distance + 8);
            telemetry.addData("Velocidade Calculada", flywheelVelocity);
            if (flywheelVelocity <= 2340) {
                outTake.setVelocity((int) flywheelVelocity);
            }
            telemetry.addData("Velocidade da flywheel", outTake.getVelocity());

            if (gamepad1.a){
                outTake.turnOff();
                return;
            }
        }
        return;
    }
    public void outTakeTriggerControl(){


        // Ideia: Nao usamos potencia muito baixa =>
        // Criar um range de uso da potencia
        // Ex: Potencia minima: A potencia para realizar um gol da menor distancia possivel
        // Ex: Potencia maxima: A potencia necessária para realizar um gol da maior distancia necessário
        // Criar uma validação de potencia critica, para proteção do motor



        double trigger = gamepad1.right_trigger;
        if (gamepad1.b) {
            isOuttakeLocked = true;

            if (trigger > 0.05) {
                lockedOuttakePower = 0.4 + (trigger * 0.6);
            } else {
                lockedOuttakePower = 0;
            }
        }
        if (gamepad1.x) {
            isOuttakeLocked = false;
        }


        if (isOuttakeLocked) {
            flywheelPower = lockedOuttakePower;

        } else {
            if (trigger > 0.05) {
                flywheelPower = 0.4 + (trigger * 0.6);
            } else {
                flywheelPower = 0;
            }
        }
        if (gamepad1.left_trigger != 0){
            flywheelPower = 0.460;
        }
        double roundedPower = Math.round(flywheelPower*1000.0)/1000.0;

        outTake.setPower(roundedPower);

    }
    public void coreHexBumperControl(){
        if (gamepad1.y) {coreHexDirection = -1;} else {coreHexDirection = 1;}
        if (gamepad1.right_bumper){ coreHexPower = 0.9 * coreHexDirection;} else {coreHexPower = 0;}
        if (gamepad1.left_bumper){ coreHex2Power = 0.9 * coreHexDirection;} else {coreHex2Power = 0;}
    }
    public void defaultPowerTriggerControl(){
        if (gamepad1.left_trigger != 0){
            flywheelPower = 0.543;
        }
    }

    }

*/