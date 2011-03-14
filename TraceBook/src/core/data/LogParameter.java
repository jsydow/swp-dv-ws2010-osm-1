package core.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author sahin
 * 
 */
public class LogParameter implements Parcelable {

    private int id = -1;
    private int deltaDistance = 0;
    private int deltaTime = 0;

    /**
     * get ID of the new Track object.
     * 
     * @return id of the Track object
     */
    public int getId() {
        return id;
    }

    /**
     * @return The distance between two positions before a new gps fix is
     *         generated
     */
    public int getDeltaDistance() {
        return deltaDistance;
    }

    /**
     * 
     * @return the time after which a new gps fix is generated
     */
    public int getDeltaTime() {
        return deltaTime;
    }

    /**
     * required for {@link Parcelable}.
     */
    public static final Parcelable.Creator<LogParameter> CREATOR = new Parcelable.Creator<LogParameter>() {

        public LogParameter createFromParcel(Parcel in) {
            return new LogParameter(in);
        }

        public LogParameter[] newArray(int arg0) {
            return null;
        }
    };

    /**
     * @param id
     * @param deltaDistance
     * @param deltaTime
     */
    public LogParameter(int id, int deltaDistance, int deltaTime) {
        this.id = id;
        this.deltaDistance = deltaDistance;
        this.deltaTime = deltaTime;
    }

    /**
     * @param in
     */
    LogParameter(Parcel in) {
        readFromParcel(in);
    }

    /**
     * Good for nothing constructor.
     */
    public LogParameter() {
        // Does nothing. Literally.
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(id);
        out.writeInt(deltaDistance);
        out.writeInt(deltaTime);
    }

    /**
     * @param in
     */
    public void readFromParcel(Parcel in) {
        id = in.readInt();
        deltaDistance = in.readInt();
        deltaTime = in.readInt();
    }
}
