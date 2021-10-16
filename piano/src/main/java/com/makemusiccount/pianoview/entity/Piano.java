package com.makemusiccount.pianoview.entity;

/*
 * Created by Gautam on 2016-11-25.
 */

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import androidx.core.content.ContextCompat;
import android.view.Gravity;

import com.makemusiccount.pianoview.R;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Piano {
    public final static int PIANO_NUMS = 24;
    private final static int BLACK_PIANO_KEY_GROUPS = 4;
    public int WHITE_PIANO_KEY_GROUPS = 4;
    private ArrayList<PianoKey[]> blackPianoKeys;
    private ArrayList<PianoKey[]> whitePianoKeys;


    private int blackKeyWidth;
    private int blackKeyHeight;
    private int whiteKeyWidth;
    private int whiteKeyHeight;
    private int width;
    private int pianoWith = 0;
    public int numShow=15;
    private float scale;
    private Context context;
    public static int autoplayviewkey;

    public Piano(Context context, float scale, int width,int numShow,int whitekey) {
        this.context = context;
        this.scale = scale;
        this.width = width;
        this.numShow = numShow;
        this.WHITE_PIANO_KEY_GROUPS = whitekey;
        initPiano();
    }

    public Drawable getWhichkey()
    {

        switch (autoplayviewkey) {
            case 1 : return new ScaleDrawable(ContextCompat.getDrawable(context, R.drawable.black_piano_key), // Black
                    Gravity.NO_GRAVITY, 1, scale).getDrawable();
            case 9 : return new ScaleDrawable(ContextCompat.getDrawable(context, R.drawable.black_piano_key_1), // Pink
                    Gravity.NO_GRAVITY, 1, scale).getDrawable();
            case 2 :return new ScaleDrawable(ContextCompat.getDrawable(context, R.drawable.black_piano_key_2), // Blue
                    Gravity.NO_GRAVITY, 1, scale).getDrawable();
             case 10 :return new ScaleDrawable(ContextCompat.getDrawable(context, R.drawable.black_piano_key_3), // yellow
                    Gravity.NO_GRAVITY, 1, scale).getDrawable();
             case 3 :return new ScaleDrawable(ContextCompat.getDrawable(context, R.drawable.black_piano_key_4),//brown
                    Gravity.NO_GRAVITY, 1, scale).getDrawable();
             case 4 :return new ScaleDrawable(ContextCompat.getDrawable(context, R.drawable.black_piano_key_5),//green
                    Gravity.NO_GRAVITY, 1, scale).getDrawable();
             case 6 :return new ScaleDrawable(ContextCompat.getDrawable(context, R.drawable.black_piano_key_6),//dark green
                    Gravity.NO_GRAVITY, 1, scale).getDrawable();
             case 7 :return new ScaleDrawable(ContextCompat.getDrawable(context, R.drawable.black_piano_key_7),//maroon
                    Gravity.NO_GRAVITY, 1, scale).getDrawable();
             case 8 :return new ScaleDrawable(ContextCompat.getDrawable(context, R.drawable.black_piano_key_8),//pink2
                    Gravity.NO_GRAVITY, 1, scale).getDrawable();
             case 5 :return new ScaleDrawable(ContextCompat.getDrawable(context, R.drawable.black_piano_key_9),//sky blue
                    Gravity.NO_GRAVITY, 1, scale).getDrawable();
            default:return new ScaleDrawable(ContextCompat.getDrawable(context, R.drawable.black_piano_key), //black
                    Gravity.NO_GRAVITY, 1, scale).getDrawable();
        }
    }

    public void initPiano() {
        blackPianoKeys = new ArrayList<>(BLACK_PIANO_KEY_GROUPS);
        whitePianoKeys = new ArrayList<>(WHITE_PIANO_KEY_GROUPS);
        if (scale > 0) {
            Drawable blackDrawable = ContextCompat.getDrawable(context, R.drawable.black_piano_key);
            Drawable whiteDrawable = ContextCompat.getDrawable(context, R.drawable.white_piano_key);

            blackDrawable=getWhichkey();
            whiteDrawable = ContextCompat.getDrawable(context, R.drawable.white_piano_key_1);

            whiteKeyWidth = (width / numShow) + 1;
            whiteKeyHeight = (int) ((float) whiteDrawable.getIntrinsicHeight() * scale);
            blackKeyWidth = (int) (whiteKeyWidth * 0.8);
            blackKeyHeight = (int) ((float) blackDrawable.getIntrinsicHeight() * scale);
            for (int i = 0; i < BLACK_PIANO_KEY_GROUPS; i++) {
                PianoKey keys[];
                switch (i) {
                    case 0:
                        keys = new PianoKey[1];
                        break;
                    case 3:
                        keys = new PianoKey[1];
                        break;
                    default:
                        keys = new PianoKey[5];
                        break;
                }
                for (int j = 0; j < keys.length; j++) {
                    keys[j] = new PianoKey();
                    Rect areaOfKey[] = new Rect[1];
                    keys[j].setType(PianoKeyType.BLACK);
                    keys[j].setGroup(i);
                    keys[j].setPositionOfGroup(j);
                    keys[j].setVoiceId(getVoiceFromResources("b" + i + j));
                    keys[j].setPressed(false);

                    switch (j) {
                        case 0:
                            keys[j].setLetterName("C#");
                            keys[j].setTopLetterName("Db");
                            break;
                        case 1:
                            keys[j].setLetterName("D#");
                            keys[j].setTopLetterName("Eb");
                            break;
                        case 2:
                            keys[j].setLetterName("F#");
                            keys[j].setTopLetterName("Gb");
                            break;
                        case 3:
                            keys[j].setLetterName("G#");
                            keys[j].setTopLetterName("Ab");
                            break;
                        case 4:
                            keys[j].setLetterName("A#");
                            keys[j].setTopLetterName("Bb");
                            break;
                    }

                    if (i == 3) {
                        keys[j].setKeyDrawable(
                                new ScaleDrawable(ContextCompat.getDrawable(context, R.drawable.black_half_piano_key),
                                        Gravity.NO_GRAVITY, 1, scale).getDrawable());
                    } else {

                        keys[j].setKeyDrawable(getWhichkey());
                    }
                    setBlackKeyDrawableBounds(i, j, keys[j].getKeyDrawable());
                    areaOfKey[0] = keys[j].getKeyDrawable().getBounds();
                    keys[j].setAreaOfKey(areaOfKey);
                    if (i == 0) {
                        keys[j].setVoice(PianoVoice.LA);
                        break;
                    }
                    switch (j) {
                        case 0:
                            keys[j].setVoice(PianoVoice.DO);
                            break;
                        case 1:
                            keys[j].setVoice(PianoVoice.RE);
                            break;
                        case 2:
                            keys[j].setVoice(PianoVoice.FA);
                            break;
                        case 3:
                            keys[j].setVoice(PianoVoice.SO);
                            break;
                        case 4:
                            keys[j].setVoice(PianoVoice.LA);
                            break;
                    }
                }
                blackPianoKeys.add(keys);
            }
            for (int i = 0; i < WHITE_PIANO_KEY_GROUPS; i++) {
                PianoKey[] mKeys;
                switch (i) {
                    case 0:
                        mKeys = new PianoKey[2];
                        break;
                    case 3:
                        mKeys = new PianoKey[1];
                        break;
                    default:
                        mKeys = new PianoKey[7];
                        break;
                }
                for (int j = 0; j < mKeys.length; j++) {
                    mKeys[j] = new PianoKey();
                    mKeys[j].setType(PianoKeyType.WHITE);
                    mKeys[j].setGroup(i);
                    mKeys[j].setPositionOfGroup(j);
                    mKeys[j].setVoiceId(getVoiceFromResources("w" + i + j));
                    mKeys[j].setPressed(false);
                    mKeys[j].setKeyDrawable( autoplayviewkey==1?
                            new ScaleDrawable(ContextCompat.getDrawable(context, R.drawable.white_piano_key_1),
                                    Gravity.NO_GRAVITY, 1, scale).getDrawable():new ScaleDrawable(ContextCompat.getDrawable(context, R.drawable.white_piano_key),
                            Gravity.NO_GRAVITY, 1, scale).getDrawable());
                    setWhiteKeyDrawableBounds(i, j, mKeys[j].getKeyDrawable());
                    pianoWith += whiteKeyWidth;
                    if (i == 0) {
                        switch (j) {
                            case 0:
                                mKeys[j].setAreaOfKey(getWhitePianoKeyArea(i, j, BlackKeyPosition.RIGHT));
                                mKeys[j].setVoice(PianoVoice.LA);
                                mKeys[j].setLetterName("A0");
                                break;
                            case 1:
                                mKeys[j].setAreaOfKey(getWhitePianoKeyArea(i, j, BlackKeyPosition.LEFT));
                                mKeys[j].setVoice(PianoVoice.SI);
                                mKeys[j].setLetterName("B0");
                                break;
                        }
                        continue;
                    }
                    if (i == 3) {
                        Rect areaOfKey[] = new Rect[1];
                        areaOfKey[0] = mKeys[j].getKeyDrawable().getBounds();
                        mKeys[j].setAreaOfKey(areaOfKey);
                        mKeys[j].setVoice(PianoVoice.DO);
                        mKeys[j].setLetterName("C");
                        break;
                    }
                    switch (j) {
                        case 0:
                            mKeys[j].setAreaOfKey(getWhitePianoKeyArea(i, j, BlackKeyPosition.RIGHT));
                            mKeys[j].setVoice(PianoVoice.DO);
                            mKeys[j].setLetterName("C");
                            break;
                        case 1:
                            mKeys[j].setAreaOfKey(getWhitePianoKeyArea(i, j, BlackKeyPosition.LEFT_RIGHT));
                            mKeys[j].setVoice(PianoVoice.RE);
                            mKeys[j].setLetterName("D");
                            break;
                        case 2:
                            mKeys[j].setAreaOfKey(getWhitePianoKeyArea(i, j, BlackKeyPosition.LEFT));
                            mKeys[j].setVoice(PianoVoice.MI);
                            mKeys[j].setLetterName("E");
                            break;
                        case 3:
                            mKeys[j].setAreaOfKey(getWhitePianoKeyArea(i, j, BlackKeyPosition.RIGHT));
                            mKeys[j].setVoice(PianoVoice.FA);
                            mKeys[j].setLetterName("F");
                            break;
                        case 4:
                            mKeys[j].setAreaOfKey(getWhitePianoKeyArea(i, j, BlackKeyPosition.LEFT_RIGHT));
                            mKeys[j].setVoice(PianoVoice.SO);
                            mKeys[j].setLetterName("G");
                            break;
                        case 5:
                            mKeys[j].setAreaOfKey(getWhitePianoKeyArea(i, j, BlackKeyPosition.LEFT_RIGHT));
                            mKeys[j].setVoice(PianoVoice.LA);
                            mKeys[j].setLetterName("A");
                            break;
                        case 6:
                            mKeys[j].setAreaOfKey(getWhitePianoKeyArea(i, j, BlackKeyPosition.LEFT));
                            mKeys[j].setVoice(PianoVoice.SI);
                            mKeys[j].setLetterName("B");
                            break;
                    }
                }
                whitePianoKeys.add(mKeys);
            }
        }
    }

    public enum PianoVoice {
        DO, RE, MI, FA, SO, LA, SI
    }

    public enum PianoKeyType {
        @SerializedName("0")
        BLACK(0), @SerializedName("1")
        WHITE(1);
        private int value;

        PianoKeyType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "PianoKeyType{" + "value=" + value + '}';
        }
    }

    private enum BlackKeyPosition {
        LEFT, LEFT_RIGHT, RIGHT
    }

    private int getVoiceFromResources(String voiceName) {
        return context.getResources().getIdentifier(voiceName, "raw", context.getPackageName());
    }

    private Rect[] getWhitePianoKeyArea(int group, int positionOfGroup, BlackKeyPosition blackKeyPosition) {
        int offset = 0;
        if (group == 0) {
            offset = 5;
        }
        switch (blackKeyPosition) {
            case LEFT:
                Rect left[] = new Rect[2];
                left[0] =
                        new Rect((7 * group - 5 + offset + positionOfGroup) * whiteKeyWidth, blackKeyHeight,
                                (7 * group - 5 + offset + positionOfGroup) * whiteKeyWidth + blackKeyWidth / 2,
                                whiteKeyHeight);
                left[1] =
                        new Rect((7 * group - 5 + offset + positionOfGroup) * whiteKeyWidth + blackKeyWidth / 2,
                                0, (7 * group - 4 + offset + positionOfGroup) * whiteKeyWidth, whiteKeyHeight);
                return left;
            case LEFT_RIGHT:
                Rect leftRight[] = new Rect[3];
                leftRight[0] =
                        new Rect((7 * group - 5 + offset + positionOfGroup) * whiteKeyWidth, blackKeyHeight,
                                (7 * group - 5 + offset + positionOfGroup) * whiteKeyWidth + blackKeyWidth / 2,
                                whiteKeyHeight);
                leftRight[1] =
                        new Rect((7 * group - 5 + offset + positionOfGroup) * whiteKeyWidth + blackKeyWidth / 2,
                                0, (7 * group - 4 + offset + positionOfGroup) * whiteKeyWidth - blackKeyWidth / 2,
                                whiteKeyHeight);
                leftRight[2] =
                        new Rect((7 * group - 4 + offset + positionOfGroup) * whiteKeyWidth - blackKeyWidth / 2,
                                blackKeyHeight, (7 * group - 4 + offset + positionOfGroup) * whiteKeyWidth,
                                whiteKeyHeight);
                return leftRight;
            case RIGHT:
                Rect right[] = new Rect[2];
                right[0] = new Rect((7 * group - 5 + offset + positionOfGroup) * whiteKeyWidth, 0,
                        (7 * group - 4 + offset + positionOfGroup) * whiteKeyWidth - blackKeyWidth / 2,
                        whiteKeyHeight);
                right[1] =
                        new Rect((7 * group - 4 + offset + positionOfGroup) * whiteKeyWidth - blackKeyWidth / 2,
                                blackKeyHeight, (7 * group - 4 + offset + positionOfGroup) * whiteKeyWidth,
                                whiteKeyHeight);
                return right;
        }
        return null;
    }

    private void setWhiteKeyDrawableBounds(int group, int positionOfGroup, Drawable drawable) {
        int offset = 0;
        if (group == 0) {
            offset = 5;
        }
        drawable.setBounds((7 * group - 5 + offset + positionOfGroup) * whiteKeyWidth, 0,
                (7 * group - 4 + offset + positionOfGroup) * whiteKeyWidth, whiteKeyHeight);
    }

    private void setBlackKeyDrawableBounds(int group, int positionOfGroup, Drawable drawable) {
        int whiteOffset = 0;
        int blackOffset = 0;
        if (group == 0) {
            whiteOffset = 5;
        }

        if (positionOfGroup == 2 || positionOfGroup == 3 || positionOfGroup == 4) {
            blackOffset = 1;
        }

        if (group == 3) {

            int left = (7 * group - 4 + whiteOffset + blackOffset + positionOfGroup) * whiteKeyWidth - blackKeyWidth / 2;
            int top = 0;
            int right = (7 * group - 4 + whiteOffset + blackOffset + positionOfGroup) * whiteKeyWidth + blackKeyWidth / 2;
            int bottom = blackKeyHeight;

            right = (left + right) / 2;

            drawable.setBounds(left, top, right, bottom);

        } else {
            drawable.setBounds((7 * group - 4 + whiteOffset + blackOffset + positionOfGroup) * whiteKeyWidth
                            - blackKeyWidth / 2, 0,
                    (7 * group - 4 + whiteOffset + blackOffset + positionOfGroup) * whiteKeyWidth
                            + blackKeyWidth / 2, blackKeyHeight);
        }
    }

    public ArrayList<PianoKey[]> getWhitePianoKeys() {
        return whitePianoKeys;
    }

    public ArrayList<PianoKey[]> getBlackPianoKeys() {
        return blackPianoKeys;
    }

    public int getPianoWith() {
        return pianoWith;
    }
}
