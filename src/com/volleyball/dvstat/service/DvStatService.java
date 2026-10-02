package com.volleyball.dvstat.service;

import com.sun.jna.Memory;
import com.volleyball.dvstat.model.TDVOutAtt;
import com.volleyball.dvstat.model.TDVOutPtsErr;
import com.volleyball.dvstat.model.TDVOutRec;
import com.volleyball.dvstat.model.TDVOutSymbol;
import com.volleyball.dvstat.model.TDVOutSymbolEx;
import com.volleyball.dvstat.model.TDVParams;
import org.springframework.stereotype.Service;
import java.io.File;

@Service
public class DvStatService {

    public VolleyballStats readStatistics(String dllPath, String statisticsFolder, int team, int player, int skill, int setNumber) {
        try {
            File dll = new File(dllPath);
            File folder = new File(statisticsFolder);

            if (!dll.isFile()) {
                return createError("DVStat.dll was not found: " + dllPath);
            }

            if (!folder.isDirectory()) {
                return createError("Statistics folder was not found: " + statisticsFolder);
            }

            DvStatLibrary library = DvStatLibrary.load(dll.getAbsolutePath());

            return readStatistics(library, statisticsFolder, team, player, skill, setNumber);

        } catch (Throwable e) {
            return createError(e);
        }
    }

    public VolleyballStats readStatistics(DvStatLibrary library, String statisticsFolder, int team, int player, int skill, int setNumber) {
        VolleyballStats stats = new VolleyballStats();

        Memory pathMemory = null;

        try {
            pathMemory = new Memory((statisticsFolder.length() + 1L) * 2L);
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
            int rc = library.GetPointsErr(params, points);
            points.read();

            if (rc != 0) {
                return createError("GetPointsErr returned " + rc);
            }

            stats.setPoints(points.Pts);
            stats.setErrors(points.Err);
            stats.setTotalEvents(points.Tot);

            TDVOutRec reception = new TDVOutRec();
            rc = library.GetReceptionPos(params, reception);
            reception.read();

            if (rc == 0) {
                stats.setReceptionTotal(reception.Tot);
                stats.setReceptionPositive(reception.Pos);
            }

            TDVOutAtt attack = new TDVOutAtt();
            rc = library.GetAttackPerc(params, attack);
            attack.read();

            if (rc == 0) {
                stats.setAttackTotal(attack.Tot);
                stats.setAttackPercentage(attack.Perc);
            }

            TDVOutSymbol symbols = new TDVOutSymbol();
            rc = library.GetSymbol(params, symbols);
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
            rc = library.GetSymbolEx(params, symbolsEx);
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
        } finally {
            if (pathMemory != null) {
                pathMemory.clear();
            }
        }

        return stats;
    }

    private VolleyballStats createError(String message) {
        VolleyballStats stats = new VolleyballStats();
        stats.setErrorMessage(message);
        return stats;
    }

    private VolleyballStats createError(Throwable e) {
        return createError(e.getClass().getSimpleName() + ": " + e.getMessage());
    }
}