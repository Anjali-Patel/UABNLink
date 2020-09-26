package com.uabn.gss.uabnlink.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Parcabe_Imagelist implements Parcelable {

    public  String image;

    public Parcabe_Imagelist() {

    }

    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(image);

    }


    private Parcabe_Imagelist(Parcel in) {
        image = in.readString();

    }
    public static final Creator<Parcabe_Imagelist> CREATOR
            = new Creator<Parcabe_Imagelist>() {

        // This simply calls our new constructor (typically private) and
        // passes along the unmarshalled `Parcel`, and then returns the new object!
        @Override
        public Parcabe_Imagelist createFromParcel(Parcel in) {
            return new Parcabe_Imagelist(in);
        }

        // We just need to copy this and change the type to match our class.
        @Override
        public Parcabe_Imagelist[] newArray(int size) {
            return new Parcabe_Imagelist[size];
        }
    };

}
