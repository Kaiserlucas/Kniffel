package kniffel;

import kniffel.data.ScoreTable;
import kniffel.gamelogic.KniffelEngine;
import kniffel.protocolBinding.KniffelSender;
import kniffel.protocolBinding.StreamBindingReceiver;
import kniffel.protocolBinding.StreamBindingSenderImpl;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class KniffelFacadeFactory {

    public static KniffelFacade produceKniffelFacade(int numberOfPlayers, String[] playerNames, int ownPlayerID, DataOutputStream[] dos, DataInputStream[] dis) {
        KniffelSender sender = new StreamBindingSenderImpl(dos,ownPlayerID == 1);
        KniffelEngine engine = new KniffelEngine(numberOfPlayers,playerNames,ownPlayerID,sender);

        //Starts the necessary receivers for the engine
        for(DataInputStream stream : dis) {
            StreamBindingReceiver receiver = new StreamBindingReceiver(stream, engine);
            receiver.start();
        }

        return engine;
    }

    public static KniffelFacade getLoadedGameFacade(ScoreTable scoreTable, DataOutputStream[] dos, DataInputStream[] dis) {
        KniffelSender sender = new StreamBindingSenderImpl(dos,scoreTable.getOwnPlayerID() == 1);
        KniffelEngine engine = new KniffelEngine(scoreTable, sender);

        //Starts the necessary receivers for the engine
        for(DataInputStream stream : dis) {
            StreamBindingReceiver receiver = new StreamBindingReceiver(stream, engine);
            receiver.start();
        }

        return engine;
    }
}
