package com.volleyball.dvstat.service;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.win32.W32APIOptions;
import com.volleyball.dvstat.model.TDVOutAtt;
import com.volleyball.dvstat.model.TDVOutPtsErr;
import com.volleyball.dvstat.model.TDVOutRec;
import com.volleyball.dvstat.model.TDVOutSymbol;
import com.volleyball.dvstat.model.TDVOutSymbolEx;
import com.volleyball.dvstat.model.TDVParams;

public interface DvStatLibrary extends Library {

    int GetPointsErr(TDVParams params, TDVOutPtsErr output);

    int GetSymbol(TDVParams params, TDVOutSymbol output);

    int GetSymbolEx(TDVParams params, TDVOutSymbolEx output);

    int GetReceptionPos(TDVParams params, TDVOutRec output);

    int GetAttackPerc(TDVParams params, TDVOutAtt output);

    static DvStatLibrary load(String dllPath) {
        return Native.load(dllPath, DvStatLibrary.class, W32APIOptions.DEFAULT_OPTIONS);
    }
}