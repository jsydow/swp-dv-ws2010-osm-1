package core.data;

import android.os.Parcel;
import android.os.Parcelable;

public class LogParameter implements Parcelable {
	public int ID = -1;
	public int delta_distance = 0;
	public int delta_time = 0;

	public static final Parcelable.Creator<LogParameter> CREATOR = new Parcelable.Creator<LogParameter>() {

		public LogParameter createFromParcel(Parcel in) {
			return new LogParameter(in);
		}

		public LogParameter[] newArray(int arg0) {
			return null;
		}
	};

	public LogParameter(int ID, int delta_distance, int delta_time) {
		this.ID = ID;
		this.delta_distance = delta_distance;
		this.delta_time = delta_time;
	}

	LogParameter(Parcel in) {
		readFromParcel(in);
	}

	public LogParameter() {
		// TODO Auto-generated constructor stub
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(ID);
		out.writeInt(delta_distance);
		out.writeInt(delta_time);
	}

	public void readFromParcel(Parcel in) {
		ID = in.readInt();
		delta_distance = in.readInt();
		delta_time = in.readInt();
	}
}
