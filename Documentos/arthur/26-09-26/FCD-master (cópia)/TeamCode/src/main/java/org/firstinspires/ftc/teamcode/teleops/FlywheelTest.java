package org.firstinspires.ftc.teamcode.teleops;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.mechanisms.Flywheel;

@TeleOp
public class FlywheelTest extends OpMode {

    Flywheel flywheel;

    public void init() {

        flywheel = new Flywheel();
        flywheel.init(hardwareMap);
    }
    public void loop() {
        flywheel.setMotorRPM(3600);

    }

    

}

