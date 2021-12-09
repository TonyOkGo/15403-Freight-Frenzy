package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

@TeleOp (name="babyTeleop", group="Pushbot")
//@Disabled
public class babyTeleop extends LinearOpMode {

    public static final double UP_POSITION = .6;
    public static final double DOWN_POSITION = 1;
    public static final double OPEN_POSITION = .9;
    public static final double CLOSED_POSITION = .3;
    static final double SPIN = 0.5;

    babyHardwareMap robot = new babyHardwareMap();
    private ElapsedTime runtime = new ElapsedTime();

    public void runOpMode() {
        robot.init(hardwareMap);
        telemetry.addData("Status", "Ready to run");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            //driving
            robot.leftDrive.setPower(-gamepad1.left_stick_y);
            robot.rightDrive.setPower(gamepad1.right_stick_y);

            if (gamepad1.left_bumper){
                robot.leftDrive.setPower(gamepad1.left_stick_y/2);
                robot.rightDrive.setPower(gamepad1.right_stick_y/2);
            }


            //extends and retracts the linear slide arm.
            if (gamepad2.a) {
                robot.armmotor.setPower(-1);
            } else if (gamepad2.b) {
                robot.armmotor.setPower(1);
            } else {
                robot.armmotor.setPower(0);
            }

            //controls the dropper servo with the x and y buttons
            if (gamepad2.x){
                robot.armservo.setPosition(DOWN_POSITION);
            }else if (gamepad2.y){
                robot.armservo.setPosition(UP_POSITION);

            }
           /* else {
                robot.armservo.setPosition(10);

            }*/
         robot.intakemotor.setPower(-gamepad2.left_stick_y/4);

            if (gamepad2.right_bumper){
                robot.intakeservo.setPosition(OPEN_POSITION);
            }else if (gamepad2.left_bumper){
                robot.intakeservo.setPosition(CLOSED_POSITION);
            }else if (gamepad1.right_bumper){
                robot.intakeservo.setPosition(CLOSED_POSITION);
            }

            /*if (gamepad1.a){
                robot.duckmotor.setPower(SPIN);
            }
            else {
                robot.duckmotor.setPower(0);
            }*/
            /*else if(gamepad1.dpad_left){
                robot.intakemotor.setPower(0.5);
            }
            else if (gamepad1.dpad_right){
                robot.intakemotor.setPower(-0.5);
            }
            else{
                robot.intakemotor.setPower(0);
            }*/
            //arm position telemetry
            telemetry.addData("position",robot.intakeservo.getPosition());
            telemetry.update();
        }
    }
}
