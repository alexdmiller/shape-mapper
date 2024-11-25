package spacefiller.shapemapper;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.opencv.opencv_java;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;
import processing.core.PMatrix3D;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.opencv.calib3d.Calib3d.Rodrigues;
import static processing.core.PApplet.radians;

public class CalibrationUtils {
  // TODO: These numbers seem flipped -- shouldn't farDist be positive?
  // TODO: how to choose these numbers?
  public static final float DEFAULT_NEAR_DIST = 10f;
  public static final float DEFAULT_FAR_DIST = -500f;

  private static boolean nativeLoaded = false;

  private static void loadNative() {
    if (!nativeLoaded) {
      Loader.load(opencv_java.class);
      nativeLoaded = true;
    }
  }

  public static GraphicsTransform calibrate(
      Map<PVector, PVector> pointMapping,
      int width,
      int height) {
    return calibrate(
        pointMapping,
        width,
        height,
        DEFAULT_NEAR_DIST,
        DEFAULT_FAR_DIST);
  }

  // Given a mapping of 3d model space points to 2d projection space points, returns the
  // calibration that, when applied to a graphics context, will achieve that mapping.
  // If the point mapping contains less than 6 points, returns an empty calibration.
  // Although a calibration is mathematically possible with 6 points, empirically I have
  // found that more points produces a better mapping.
  public static GraphicsTransform calibrate(
      Map<PVector, PVector> pointMapping,
      int width,
      int height,
      float nearDist,
      float farDist) {
    loadNative();

    Mat translation;
    Mat rotation;

    if (pointMapping.size() < 6) {
      return GraphicsTransform.empty();
    }

    // Prepare inputs to pass into OpenCV calibrateCamera function

    Mat objectPoints = new MatOfPoint3f();
    Mat imagePoints = new MatOfPoint3f();

    for (PVector referencePoint : pointMapping.keySet()) {
      PVector imagePoint = pointMapping.get(referencePoint);
      objectPoints.push_back(new MatOfPoint3f(new Point3(referencePoint.x, referencePoint.y, referencePoint.z)));
      imagePoints.push_back(new MatOfPoint2f(new Point(imagePoint.x, imagePoint.y)));
    }

    float aov = 80;
    Size imageSize = new Size(width, height);
    float f = (float) (imageSize.width * radians(aov));
    Point c = new Point(imageSize.width / 2.0, imageSize.height / 2.0);

    Mat cameraMatrix = new Mat(3, 3, CvType.CV_32FC1);
    cameraMatrix.put(0, 0,
        f, 0, c.x,
        0, f, c.y,
        0, 0, 1);

    Mat distCoeffs = new Mat();

    List<Mat> rvecs = new ArrayList<>();
    List<Mat> tvecs = new ArrayList<>();

    ArrayList<Mat> objectPointViews = new ArrayList<>();
    objectPointViews.add(objectPoints);

    ArrayList<Mat> imagePointViews = new ArrayList<>();
    imagePointViews.add(imagePoints);

    int flags = Calib3d.CALIB_FIX_ASPECT_RATIO
        | Calib3d.CALIB_FIX_K1
        | Calib3d.CALIB_FIX_K2
        | Calib3d.CALIB_FIX_K3
        | Calib3d.CALIB_ZERO_TANGENT_DIST
        | Calib3d.CALIB_USE_INTRINSIC_GUESS;

    // `calibrateCamera` writes its output into the `rvecs` and `tvecs` matrices
    Calib3d.calibrateCamera(
        objectPointViews,
        imagePointViews,
        imageSize,
        cameraMatrix,
        distCoeffs,
        rvecs,
        tvecs,
        flags);

    translation = tvecs.get(0);
    rotation = rvecs.get(0);

    // Prepare intput for calibrationMatrixValues function
    double[] fovOutputX = new double[1];
    double[] fovOutputY = new double[1];
    double[] focalLengthOutput = new double[1];
    Point principalPoint = new Point();
    double[] aspectRatioOutput = new double[1];

    // Call calibrationMatrixValues to get the `principalPoint` value (we don't use the
    // other outputs of this function currently)
    Calib3d.calibrationMatrixValues(
        cameraMatrix,
        imageSize,
        0, 0,
        fovOutputX,
        fovOutputY,
        focalLengthOutput,
        principalPoint,
        aspectRatioOutput);

    // Use the outputs of `calibrateCamera` and `calibrationMatrixValues` to prepare
    // a projection matrix and model view matrix which can be applied to Processing
    // graphics context

    double fx = cameraMatrix.get(0, 0)[0];
    double fy = cameraMatrix.get(1, 1)[0];
    double cx = principalPoint.x;
    double cy = principalPoint.y;

    float left = (float) (nearDist * (-cx) / fx);
    float right = (float) (nearDist * (width - cx) / fx);
    float bottom = (float) (nearDist * (cy) / fy);
    float top = (float) (nearDist * (cy - height) / fy);

    float n2 = 2.0F * nearDist;
    float w = right - left;
    float h = top - bottom;
    float d = farDist - nearDist;

    PMatrix3D projectionMatrix = new PMatrix3D(
        1, 0, 0, 0,
        0, 1, 0, 0,
        0, 0, 1, 0,
        0, 0, 0, 1
    );

    PMatrix3D frustrum = new PMatrix3D(
        n2 / w, 0.0F, (right + left) / w, 0.0F,
        0.0F, -n2 / h, -(top + bottom) / h, 0.0F,
        0.0F, 0.0F, -(farDist + nearDist) / d, -(n2 * farDist) / d,
        0.0F, 0.0F, -1.0F, 0.0F);

    projectionMatrix.apply(frustrum);

    PMatrix3D modelViewMatrix = makeModelMatrix(rotation, translation);
    return new GraphicsTransform(projectionMatrix, modelViewMatrix);
  }

  // Helper function that takes the rotation and translation vector produced by
  // `calibrateCamera` and returns a Processing matrix representing those transformations
  public static PMatrix3D makeModelMatrix(Mat rotationVector, Mat translation) {
    Mat rotationMatrix = new Mat();
    Rodrigues(rotationVector, rotationMatrix);

    double[] tm = new double[3];
    translation.get(0, 0, tm);

    double[] rm = new double[16];
    rotationMatrix.get(0, 0, rm);

    PMatrix3D matrix = new PMatrix3D(
        (float) rm[0], (float) rm[3], (float) rm[6], 0.0f,
        (float) rm[1], (float) rm[4], (float) rm[7], 0.0f,
        (float) rm[2], (float) rm[5], (float) rm[8], 0.0f,
        (float) tm[0], (float) tm[1], (float) tm[2], 1.0f);
    matrix.transpose();
    return matrix;
  }
}
