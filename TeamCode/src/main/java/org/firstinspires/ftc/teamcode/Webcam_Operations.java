package org.firstinspires.ftc.teamcode;

import static android.graphics.Color.alpha;
import static android.graphics.Color.blue;
import static android.graphics.Color.green;
import static android.graphics.Color.red;

import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.android.util.Size;
import org.firstinspires.ftc.robotcore.external.function.Consumer;
import org.firstinspires.ftc.robotcore.external.function.Continuation;
import org.firstinspires.ftc.robotcore.external.hardware.camera.Camera;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraCaptureRequest;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraCaptureSequenceId;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraCaptureSession;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraCharacteristics;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraException;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraFrame;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraManager;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.internal.collections.EvictingBlockingQueue;
import org.firstinspires.ftc.robotcore.internal.network.CallbackLooper;
import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
import org.firstinspires.ftc.robotcore.internal.system.ContinuationSynchronizer;
import org.firstinspires.ftc.robotcore.internal.system.Deadline;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

@TeleOp(name="Webcam Operations", group ="LinearOpMode")
public class Webcam_Operations extends LinearOpMode {

    //Frame box for region A (1):
    private static final int REGIONA_MINX = 1;
    private static final int REGIONA_MAXX = 640;
    private static final int REGIONA_MINY = 1;
    private static final int REGIONA_MAXY = 480;
    //Frame box for region B (2):
    private static final int REGIONB_MINX = 1;
    private static final int REGIONB_MAXX = 10;
    private static final int REGIONB_MINY = 1;
    private static final int REGIONB_MAXY = 10;
    //Frame box for region C (3):
    private static final int REGIONC_MINX = 1;
    private static final int REGIONC_MAXX = 10;
    private static final int REGIONC_MINY = 1;
    private static final int REGIONC_MAXY = 10;

    int maxAll = getColorInt(255, 255, 255, 255);
    int minAll = getColorInt(255, 0, 0, 0);
    int maxRed = getColorInt(255, 255, 150, 150);
    int minRed = getColorInt(255, 150, 0, 0);
    int maxBlue = getColorInt(255, 150, 150, 255);
    int minBlue = getColorInt(255, 0, 0, 130);
    int maxWhite = getColorInt(255, 92, 255, 228);
    int minWhite = getColorInt(255, 25, 181, 155);

    //Width: 640 , Height: 480


    private static final String TAG = "Webcam Sample";
    private static final int secondsPermissionTimeout = 100;

    private CameraManager cameraManager;
    private WebcamName cameraName;
    private Camera camera;
    private CameraCaptureSession cameraCaptureSession;

    private EvictingBlockingQueue<Bitmap> frameQueue;

    /** State regarding where and how to save frames when the 'A' button is pressed.
    private int captureCounter = 0;
    private File captureDirectory = AppUtil.ROBOT_DATA_DIR;*/

    /** A utility object that indicates where the asynchronous callbacks from the camera
     * infrastructure are to run. In this OpMode, that's all hidden from you (but see {@link #startCamera}
     * if you're curious): no knowledge of multi-threading is needed here. */
    private Handler callbackHandler;

    //----------------------------------------------------------------------------------------------
    // Main OpMode entry
    //----------------------------------------------------------------------------------------------

    @Override
    public void runOpMode() throws InterruptedException {
        int val = 0;
        Bitmap bmp = null;
        telemetry.addLine("Waiting for start...");
        telemetry.update();
        waitForStart();
        while(opModeIsActive()) {
            bmp = getBarcodeBitmap();
            /*telemetry.addLine("Full Width: " + bmp.getWidth());
            telemetry.addLine("Full Height: " + bmp.getHeight());*/
            //For ALL Colors
            //val = barcodeValue(bmp, minAll, maxAll);
            //For Red Tape
            val = barcodeValue(bmp, minRed, maxRed);
            //For Blue Tape
            //val = barcodeValue(bmp, minBlue, maxBlue);
            //For Team Marker
            //val = barcodeValue(bmp, minWhite, maxWhite);
            telemetry.addLine("Direction Val: " + val);
            telemetry.update();
        }
    }

    public void visionInit() { }

    public int barcodeValue(Bitmap frameMap, int targetColorMin, int targetColorMax) {
        //Divide main bitmap into 3 subsets
        //Bitmap A
        telemetry.addLine("Attempting to divide bitmap...");
        telemetry.update();
        int aHeight = REGIONA_MAXY - REGIONA_MINY;
        int aWidth = REGIONA_MAXX - REGIONA_MINX;
        Bitmap bitmapA = Bitmap.createBitmap(frameMap, REGIONA_MINX, REGIONA_MINY, aWidth, aHeight);
        if(bitmapA != null) {
            telemetry.addLine("bitmapA created.");
        }
        else {
            telemetry.addLine("Failed to create bitmapA");
        }
        //Bitmap B
        int bHeight = REGIONB_MAXY - REGIONB_MINY;
        int bWidth = REGIONB_MAXX - REGIONB_MINX;
        Bitmap bitmapB = Bitmap.createBitmap(frameMap, REGIONB_MINX, REGIONB_MINY, bWidth, bHeight);
        if(bitmapA != null) {
            telemetry.addLine("bitmapB created.");
        }
        else {
            telemetry.addLine("Failed to create bitmapB");
        }
        //Bitmap C
        int cHeight = REGIONC_MAXY - REGIONC_MINY;
        int cWidth = REGIONC_MAXX - REGIONC_MINX;
        Bitmap bitmapC = Bitmap.createBitmap(frameMap, REGIONC_MINX, REGIONC_MINY, cWidth, cHeight);
        if(bitmapA != null) {
            telemetry.addLine("bitmapC created.");
        }
        else {
            telemetry.addLine("Failed to create bitmapC");
        }

        telemetry.addLine("Bitmap divided. Attempting to count pixels...");
        telemetry.update();
        //Get how many pixels fall within target color for each bitmap
        int aPixels = pixelsColor(bitmapA, targetColorMin, targetColorMax);
        telemetry.addLine("aPixels has been counted.");
        //==========
        sleep(10000);
        //==========
        int bPixels = pixelsColor(bitmapB, targetColorMin, targetColorMax);
        telemetry.addLine("bPixels has been counted.");
        int cPixels = pixelsColor(bitmapC, targetColorMin, targetColorMax);
        telemetry.addLine("cPixels has been counted.");

        telemetry.addLine("Pixels counted. Attempting to compare counts");
        telemetry.update();
        if(aPixels > bPixels && aPixels > cPixels) {
            return 1;
        }
        else if(bPixels > aPixels && bPixels > cPixels) {
            return 2;
        }
        else if(cPixels > bPixels && cPixels > aPixels) {
            return 3;
        }
        else if(aPixels > 0 || bPixels > 0 || cPixels > 0) {
            return 4;
        }
        return 0;
    }

    public int getColorInt(int alphaVal, int redVal, int greenVal, int blueVal) {
        int combColor = (alphaVal & 0xff) << 24 | (redVal & 0xff) << 16 | (greenVal & 0xff) << 8 | (blueVal & 0xff);
        return combColor;
    }

    public int pixelsColor(Bitmap frameMap, int colorMin, int colorMax) {
        int pixelCount = 0;
        int minR = red(colorMin);
        int minG = green(colorMin);
        int minB = blue(colorMin);
        int maxR = red(colorMax);
        int maxG = green(colorMax);
        int maxB = blue(colorMax);
        telemetry.addLine("Color Values retrieved. Proceeding to count pixels...");
        for(int i = 1; i < frameMap.getHeight(); i++) {
            for(int j = 1; j < frameMap.getWidth(); j++) {
                int curPixel = frameMap.getPixel(j, i);
                int pR = red(curPixel);
                int pG = green(curPixel);
                int pB = blue(curPixel);
                if(pR >= minR && pR <= maxR) {
                    if(pG >= minG && pG <= maxG) {
                        if(pB >= minB && pB <= maxB) {
                            pixelCount++;
                        }
                    }
                }
            }
        }
        telemetry.addLine("Pixels counted: " + pixelCount);
        telemetry.update();
        return pixelCount;
    }

    public Bitmap getBarcodeBitmap() {

        callbackHandler = CallbackLooper.getDefault().getHandler();

        cameraManager = ClassFactory.getInstance().getCameraManager();
        cameraName = hardwareMap.get(WebcamName.class, "Webcam 1");

        initializeFrameQueue(2);
        //AppUtil.getInstance().ensureDirectoryExists(captureDirectory);

        Bitmap bmp = null;

        try {
            //telemetry.addLine("Attempting to Open Camera...");
            openCamera();
            if (camera == null) return null;
            //telemetry.addLine("Camera Opened. Attempting to Start Camera...");
            startCamera();
            if (cameraCaptureSession == null) return null;
            //telemetry.addLine("Camera Started. Attempting to pull bmp from poll...");
            telemetry.update();
            boolean continueCaptureAttempt = true;
            while(continueCaptureAttempt == true) {
                bmp = frameQueue.poll();
                if (bmp != null) {
                    continueCaptureAttempt = false;
                    onNewFrame(bmp);
                    telemetry.addLine("bitmap pulled from camera");
                } else {
                    telemetry.addLine("Failed to pull bitmap. Null from poll");
                }
                telemetry.update();
            }
            telemetry.update();
        } finally {
            closeCamera();
            telemetry.addLine("Camera Close.");
            telemetry.update();
        }
        return bmp;
    }

    private void onNewFrame(Bitmap frame) {
        //saveBitmap(frame);
        //frame.recycle(); // not strictly necessary, but helpful
    }



    //----------------------------------------------------------------------------------------------
    // Camera operations
    //----------------------------------------------------------------------------------------------

    private void initializeFrameQueue(int capacity) {
        /** The frame queue will automatically throw away bitmap frames if they are not processed
         * quickly by the OpMode. This avoids a buildup of frames in memory */
        frameQueue = new EvictingBlockingQueue<Bitmap>(new ArrayBlockingQueue<Bitmap>(capacity));
        frameQueue.setEvictAction(new Consumer<Bitmap>() {
            @Override public void accept(Bitmap frame) {
                // RobotLog.ii(TAG, "frame recycled w/o processing");
                frame.recycle(); // not strictly necessary, but helpful
            }
        });
    }

    private void openCamera() {
        if (camera != null) return; // be idempotent

        Deadline deadline = new Deadline(secondsPermissionTimeout, TimeUnit.SECONDS);
        camera = cameraManager.requestPermissionAndOpenCamera(deadline, cameraName, null);
        if (camera == null) {
            error("camera not found or permission to use not granted: %s", cameraName);
        }
    }

    private void startCamera() {
        if (cameraCaptureSession != null) return; // be idempotent

        /** YUY2 is supported by all Webcams, per the USB Webcam standard: See "USB Device Class Definition
         * for Video Devices: Uncompressed Payload, Table 2-1". Further, often this is the *only*
         * image format supported by a camera */
        final int imageFormat = ImageFormat.YUY2;

        /** Verify that the image is supported, and fetch size and desired frame rate if so */
        CameraCharacteristics cameraCharacteristics = cameraName.getCameraCharacteristics();
        if (!contains(cameraCharacteristics.getAndroidFormats(), imageFormat)) {
            error("image format not supported");
            return;
        }
        final Size size = cameraCharacteristics.getDefaultSize(imageFormat);
        final int fps = cameraCharacteristics.getMaxFramesPerSecond(imageFormat, size);

        /** Some of the logic below runs asynchronously on other threads. Use of the synchronizer
         * here allows us to wait in this method until all that asynchrony completes before returning. */
        final ContinuationSynchronizer<CameraCaptureSession> synchronizer = new ContinuationSynchronizer<>();
        try {
            /** Create a session in which requests to capture frames can be made */
            camera.createCaptureSession(Continuation.create(callbackHandler, new CameraCaptureSession.StateCallbackDefault() {
                @Override public void onConfigured(@NonNull CameraCaptureSession session) {
                    try {
                        /** The session is ready to go. Start requesting frames */
                        final CameraCaptureRequest captureRequest = camera.createCaptureRequest(imageFormat, size, fps);
                        session.startCapture(captureRequest,
                                new CameraCaptureSession.CaptureCallback() {
                                    @Override public void onNewFrame(@NonNull CameraCaptureSession session, @NonNull CameraCaptureRequest request, @NonNull CameraFrame cameraFrame) {
                                        /** A new frame is available. The frame data has <em>not</em> been copied for us, and we can only access it
                                         * for the duration of the callback. So we copy here manually. */
                                        Bitmap bmp = captureRequest.createEmptyBitmap();
                                        cameraFrame.copyToBitmap(bmp);
                                        frameQueue.offer(bmp);
                                    }
                                },
                                Continuation.create(callbackHandler, new CameraCaptureSession.StatusCallback() {
                                    @Override public void onCaptureSequenceCompleted(@NonNull CameraCaptureSession session, CameraCaptureSequenceId cameraCaptureSequenceId, long lastFrameNumber) {
                                        RobotLog.ii(TAG, "capture sequence %s reports completed: lastFrame=%d", cameraCaptureSequenceId, lastFrameNumber);
                                    }
                                })
                        );
                        synchronizer.finish(session);
                    } catch (CameraException|RuntimeException e) {
                        RobotLog.ee(TAG, e, "exception starting capture");
                        error("exception starting capture");
                        session.close();
                        synchronizer.finish(null);
                    }
                }
            }));
        } catch (CameraException|RuntimeException e) {
            RobotLog.ee(TAG, e, "exception starting camera");
            error("exception starting camera");
            synchronizer.finish(null);
        }

        /** Wait for all the asynchrony to complete */
        try {
            synchronizer.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        /** Retrieve the created session. This will be null on error. */
        cameraCaptureSession = synchronizer.getValue();
    }

    private void stopCamera() {
        if (cameraCaptureSession != null) {
            cameraCaptureSession.stopCapture();
            cameraCaptureSession.close();
            cameraCaptureSession = null;
        }
    }

    private void closeCamera() {
        stopCamera();
        if (camera != null) {
            camera.close();
            camera = null;
        }
    }

    //----------------------------------------------------------------------------------------------
    // Utilities
    //----------------------------------------------------------------------------------------------

    private void error(String msg) {
        telemetry.log().add(msg);
        telemetry.update();
    }
    private void error(String format, Object...args) {
        telemetry.log().add(format, args);
        telemetry.update();
    }

    private boolean contains(int[] array, int value) {
        for (int i : array) {
            if (i == value) return true;
        }
        return false;
    }
}
