package com.volleyball.dvstat.service;

import com.volleyball.dvstat.model.*;
import com.sun.jna.Memory;
import org.springframework.stereotype.Service;
import java.io.File;

@Service
public class DvStatService {

    public VolleyballStats readStatistics(String dllPath, String statisticsFolder, int team, int player, int skill, int setNumber) {
        VolleyballStats stats = new VolleyballStats();

        try {
            File dll = new File(dllPath);
            File folder = new File(statisticsFolder);

            if (!dll.isFile()) {
                throw new IllegalArgumentException("DVStat.dll was not found: " + dllPath);
            }

            if (!folder.isDirectory()) {
                throw new IllegalArgumentException("Statistics folder was not found: " + statisticsFolder);
            }

            DvStatLibrary lib = DvStatLibrary.load(dll.getAbsolutePath());

            Memory pathMemory = new Memory((statisticsFolder.length() + 1L) * 2L);
            pathMemory.setWideString(0, statisticsFolder);

            TDVParams params = new TDVParams();
            params.PathSource = pathMemory;
            params.IdTeam = team;
            params.IdPlayer = player;
            params.Skill = skill;
            params.SetN = setNumber;
            params.IDCall = 0;
            params.write();

            TDVOutPtsErr points = new TDVOutPtsErr();
            int rc = lib.GetPointsErr(params, points);
            points.read();

            if (rc != 0) {
                throw new IllegalStateException("GetPointsErr returned " + rc);
            }

            stats.setPoints(points.Pts);
            stats.setErrors(points.Err);
            stats.setTotalEvents(points.Tot);

            TDVOutRec reception = new TDVOutRec();
            rc = lib.GetReceptionPos(params, reception);
            reception.read();

            if (rc == 0) {
                stats.setReceptionTotal(reception.Tot);
                stats.setReceptionPositive(reception.Pos);
            }

            TDVOutAtt attack = new TDVOutAtt();
            rc = lib.GetAttackPerc(params, attack);
            attack.read();

            if (rc == 0) {
                stats.setAttackTotal(attack.Tot);
                stats.setAttackPercentage(attack.Perc);
            }

            TDVOutSymbol symbols = new TDVOutSymbol();
            rc = lib.GetSymbol(params, symbols);
            symbols.read();

            if (rc == 0) {
                stats.setMm(symbols.MM);
                stats.setM(symbols.M);
                stats.setS(symbols.S);
                stats.setP(symbols.P);
                stats.setD(symbols.D);
                stats.setE(symbols.E);
                stats.setTotalSymbols(symbols.Tot);
            }

            TDVOutSymbolEx symbolsEx = new TDVOutSymbolEx();
            rc = lib.GetSymbolEx(params, symbolsEx);
            symbolsEx.read();

            if (rc == 0) {
                stats.setMm(symbolsEx.MM);
                stats.setMmp(symbolsEx.MMP);
                stats.setMmc(symbolsEx.MMC);
                stats.setM(symbolsEx.M);
                stats.setS(symbolsEx.S);
                stats.setSp(symbolsEx.SP);
                stats.setSc(symbolsEx.SC);
                stats.setP(symbolsEx.P);
                stats.setD(symbolsEx.D);
                stats.setDp(symbolsEx.DP);
                stats.setDc(symbolsEx.DC);
                stats.setE(symbolsEx.E);
                stats.setTotalSymbols(symbolsEx.Tot);
            }

        } catch (Throwable e) {
            stats.setErrorMessage(e.getClass().getSimpleName() + ": " + e.getMessage());
        }

        return stats;
    }
}