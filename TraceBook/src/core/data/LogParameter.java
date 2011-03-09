package core.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author sahin
 * 
 */
public class LogParameter implements Parcelable {
    /**
	 * 
	 */
    public int id = -1;
    /**
	 * 
	 */
    public int deltaDistance = 0;
    /**
	 * 
	 */
    public int deltaTime = 0;

    /**
	 * 
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
	 * 
	 */
    public LogParameter() {
        // TODO Auto-generated constructor stub
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
