package com.volleyball.dvstat.service;

import com.volleyball.dvstat.model.*;
import com.sun.jna.Native;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/** Native mapping for Genius Sports DVStat.dll 2.1. */
public interface DvStatLibrary extends StdCallLibrary {
    int GetPointsErr(TDVParams params, TDVOutPtsErr output);
    int GetSymbol(TDVParams params, TDVOutSymbol output);
    int GetSymbolEx(TDVParams params, TDVOutSymbolEx output);
    int GetReceptionPos(TDVParams params, TDVOutRec output);
    int GetAttackPerc(TDVParams params, TDVOutAtt output);

    static DvStatLibrary load(String dllPath) {
        return Native.load(dllPath, DvStatLibrary.class, W32APIOptions.DEFAULT_OPTIONS);
    }
}
