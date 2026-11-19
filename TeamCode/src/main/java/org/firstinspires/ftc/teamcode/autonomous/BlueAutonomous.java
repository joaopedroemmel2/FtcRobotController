package org.firstinspires.ftc.teamcode.autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.mechanisms.MecanumDrive;


@Autonomous
public class BlueAutonomous extends LinearOpMode {

    MecanumDrive mecanum;
    enum State{
        WALKING,
        SHOOTING,
        TURNING,
        GET_ARTIFACTS,
        WALKING_BACK_TO_GOAL,
        TURN_TO_GOAL,
        SHOOTING_ARTIFACTS,
        FINISHED

    }
    State state = State.WALKING;
    long runningTimer = 0;

    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addLine("Inicializando...");
        telemetry.update();

        // init
        mecanum = new MecanumDrive();
        mecanum.init(hardwareMap);

        telemetry.addLine("Pronto! Aperte PLAY.");
        telemetry.update();

        waitForStart();

        if (isStopRequested()) return;
        telemetry.addLine("Rodando...");
        telemetry.update();

        //Início do processo





        while (opModeIsActive()) {

            telemetry.addData("Status", "Rodando");
            telemetry.addData("Estado", state.toString());

            telemetry.addData("Corrente dos motores de movimento(Esquerdo/Direito)", mecanum.getCurrents());


  


            //Ações em loop
            switch (state) {
                case WALKING:
                    moveForward(-100);
                    state = State.SHOOTING;
                    break;
                case SHOOTING:
                    break;
                case TURNING:
                    rotate(-110);
                    Thread.sleep(500);
                    state = State.GET_ARTIFACTS;
                    break;
                case GET_ARTIFACTS:
                    state = State.WALKING_BACK_TO_GOAL;
                    break;
                case WALKING_BACK_TO_GOAL:
                    moveForward(104); //Anda de volta
                    state = State.TURN_TO_GOAL;
                    break;
                case TURN_TO_GOAL:

                    //Sugestão: (com ajuste com ângulo da câmera)
                    //double anguloAjuste = cam.getYaw();
                    //while (anguloAjuste != 0){
                    //    rotate(anguloAjuste);
                    //    anguloAjuste = cam.getYaw();
                    //}

                    rotate(110);
                    Thread.sleep(500);
                    state = State.SHOOTING_ARTIFACTS;
                    break;
                case SHOOTING_ARTIFACTS:
                    break;
                case FINISHED:
                    requestOpModeStop();
                    return;
            }


        }
    }
    public void moveForward(double distanceCm) {

        // -------------------------
        // CONSTANTES DO MOTOR/RODA
        // -------------------------

        double wheelDiameter = 9;
        double cpr = 560;

        double wheelCircumference = Math.PI * wheelDiameter;
        int targetPulses = (int)((distanceCm / wheelCircumference) * cpr);

        // -------------------------
        // PREPARAR OS ENCODERS
        // -------------------------
        mecanum.setMotorModes(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        mecanum.setTargetPositions(targetPulses, targetPulses, targetPulses, targetPulses);

        mecanum.setMotorModes(DcMotor.RunMode.RUN_TO_POSITION);

        // Velocidade de movimento (0 a 1)
        mecanum.setPowers(0.5, 0.5, 0.5, 0.5);

        // -------------------------
        // LOOP DE MOVIMENTO (bloqueante)
        // -------------------------
        while (opModeIsActive()) {

            telemetry.addData("Posições(E / D)", mecanum.getPositions().toString());
            telemetry.update();
        }

        // -------------------------
        // PARAR OS MOTORES
        // -------------------------
        mecanum.stopMotors();

        mecanum.setMotorModes(DcMotor.RunMode.RUN_USING_ENCODER);
    }
    public void rotate(double angleDeg) {

        // -------------------------------------
        // CONSTANTES DO ROBÔ
        // -------------------------------------
        int targetPulses = getTargetPulses(angleDeg);

        // -------------------------------------
        // PREPARAR OS ENCODERS
        // -------------------------------------
        mecanum.setMotorModes(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // Para girar, uma roda vai pra frente e outra pra trás
        mecanum.setTargetPositions(targetPulses, -targetPulses, targetPulses, -targetPulses);

        mecanum.setMotorModes(DcMotor.RunMode.RUN_TO_POSITION);

        // Velocidade do giro
        mecanum.setPowers(0.5, 0.5, 0.5, 0.5);

        // -------------------------------------
        // LOOP BLOQUEANTE
        // -------------------------------------
        while (opModeIsActive()) {

            telemetry.addData("Alvos(E / D)", "L: " + targetPulses + " | R: " + -targetPulses);
            telemetry.addData("Posições(E / D)", mecanum.getPositions().toString());
            telemetry.update();
        }

        // -------------------------------------
        // PARAR MOTORES
        // -------------------------------------
        mecanum.stopMotors();

        mecanum.setMotorModes(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    private static int getTargetPulses(double angleDeg) {
        double wheelDiameter = 9.0;
        double cpr = 560;
        double trackWidth = 37.0;

        double wheelCircumference = Math.PI * wheelDiameter;

        // Circunferência do círculo descrito pelo giro do robô
        double turnCircumference = Math.PI * trackWidth;

        // Distância linear que cada roda precisa percorrer
        double distancePerWheel = (angleDeg / 360.0) * turnCircumference;

        // Converter distância → pulsos
        return (int)((distancePerWheel / wheelCircumference) * cpr);
    }

}
