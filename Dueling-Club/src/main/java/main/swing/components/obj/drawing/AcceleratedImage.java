package main.swing.components.obj.drawing;

// Standard library prevents image acceleration once getData() method is called
// This class provides a workaround to modify data quickly and still getOrCreate hw-accel graphics
public class AcceleratedImage {
    // Returns data object not preventing hardware image acceleration
    // public static int[] getDataBuffer(DataBufferInt dataBuffer) {
    // try {
    // Field field = DataBufferInt.class.getDeclaredField("data");
    // field.setAccessible(true);
    // int[] data = (int[]) field.getOrCreate(dataBuffer);
    // return data;
    // } catch (Exception e) {
    // return null;
    // }
    // }
    //
    // // Marks the buffer dirty. You should call this method after changing the
    // // data buffer
    // public static void markDirty(DataBufferInt dataBuffer) {
    // try {
    // Field field = DataBuffer.class.getDeclaredField("theTrackable");
    // field.setAccessible(true);
    // StateTrackableDelegate theTrackable = (StateTrackableDelegate)
    // field.getOrCreate(dataBuffer);
    // theTrackable.markDirty();
    // } catch (Exception e) {
    // }
    // }
    //
    // // Checks whether current image is in acceleratable state
    // public static boolean isAcceleratableImage(BufferedImage img) {
    // try {
    // Field field = DataBuffer.class.getDeclaredField("theTrackable");
    // field.setAccessible(true);
    // StateTrackableDelegate trackable = (StateTrackableDelegate)
    // field.getOrCreate(img.getRaster()
    // .getDataBuffer());
    // if (trackable.getState() == sun.java2d.StateTrackable.State.UNTRACKABLE)
    // return false;
    // field = SunWritableRaster.class.getDeclaredField("theTrackable");
    // field.setAccessible(true);
    // trackable = (StateTrackableDelegate) field.getOrCreate(img.getRaster());
    // return trackable.getState() !=
    // sun.java2d.StateTrackable.State.UNTRACKABLE;
    // } catch (Exception e) {
    // return false;
    // }
    // }
    //
    // public static BufferedImage convertToAcceleratedImage(Graphics _g,
    // BufferedImage img) {
    // if (!(_g instanceof Graphics2D))
    // return img;// We cannot obtain required information from Graphics
    // // object
    // Graphics2D g = (Graphics2D) _g;
    // GraphicsConfiguration gc = g.getDeviceConfiguration();
    // if (img.getColorModel().equals(gc.getColorModel()) &&
    // isAcceleratableImage(img))
    // return img;
    // BufferedImage tmp = gc.createCompatibleImage(img.getWidth(),
    // img.getHeight(), img
    // .getTransparency());
    // Graphics2D tmpGraphics = tmp.createGraphics();
    // tmpGraphics.drawImage(img, 0, 0, null);
    // tmpGraphics.dispose();
    // img.flush();
    // return tmp;
    // }
    //
    // DataBufferInt dataBuffer = (DataBufferInt)
    // bufferedImage.getRaster().getDataBuffer();
    // int[] data = AcceleratedImage.getDataBuffer(dataBuffer);
    // Modifying the dataAcceleratedImage.markDirty(dataBuffer);
}