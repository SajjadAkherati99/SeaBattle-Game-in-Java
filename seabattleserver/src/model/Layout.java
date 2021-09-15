package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

public class Layout {
    private LinkedList<Integer> layout;
    private int [] shipsLives = new int[]{4, 3, 3, 2, 2, 2, 1, 1, 1, 1};
    private final int [] shipKind = new int[]{0, 1, 1, 2, 2, 2, 3, 3, 3, 3};
    private boolean [] shipDestroyed = new boolean[]{false, false, false, false, false,
                                                        false, false, false, false, false};
    private int numberOfAliveShips = 10;

    public Layout() {
        Random random = new Random();
        layout = new LinkedList<>();

        LinkedList<Integer> unUsed_Cruiser = new LinkedList<>();
        LinkedList<Integer> unUsed_Destroyer = new LinkedList<>();
        LinkedList<Integer> unUsed_Frigate = new LinkedList<>();

        for (int i = 0; i < 256; i++) {
            if ((i % 16) < 13)
                unUsed_Cruiser.add(i);
            if ((i % 16) < 14)
                unUsed_Destroyer.add(i);
            unUsed_Frigate.add(i);
        }


        int place_start = (int)(random.nextInt(13) + 16*random.nextInt(16)); //
        layout.add((int)place_start);
        unUsed_Cruiser = updateUnUsed(unUsed_Cruiser, place_start, 4, 3);
        unUsed_Destroyer = updateUnUsed(unUsed_Destroyer, place_start, 4, 2);
        unUsed_Frigate = updateUnUsed(unUsed_Frigate, place_start, 4, 1);

//        System.out.println(unUsed_Cruiser);
//        System.out.println(unUsed_Destroyer);
//        System.out.println(unUsed_Frigate);

        for (int i = 0; i < 2; i++){
            place_start = unUsed_Cruiser.get(random.nextInt(unUsed_Cruiser.size()));
            layout.add(place_start);
            unUsed_Cruiser = updateUnUsed(unUsed_Cruiser, place_start, 3, 3);
            unUsed_Destroyer = updateUnUsed(unUsed_Destroyer, place_start, 3, 2);
            unUsed_Frigate = updateUnUsed(unUsed_Frigate, place_start, 3, 1);
        }

        for (int i = 0; i < 3; i++){
            place_start = unUsed_Destroyer.get(random.nextInt(unUsed_Destroyer.size()));
            layout.add(place_start);
            unUsed_Destroyer = updateUnUsed(unUsed_Destroyer, place_start, 2, 2);
            unUsed_Frigate = updateUnUsed(unUsed_Frigate, place_start, 2, 1);
        }

        for (int i = 0; i < 4; i++){
            place_start = unUsed_Frigate.get(random.nextInt(unUsed_Frigate.size()));
            layout.add(place_start);
            unUsed_Frigate = updateUnUsed(unUsed_Frigate, place_start, 1, 1);
        }
    }

    public LinkedList<Integer> updateUnUsed(LinkedList<Integer> unUsed, int place_start, int dc, int trh){
        LinkedList<Integer> newUnUsed = new LinkedList<>();
        for (int i : unUsed){
            boolean check = true;
            for (int j = place_start-trh; j <= (place_start+dc); j++){
                if ((i == (j-16)) || (i == (j)) || (i == (j+16))) {
                    check = false;
                }
            }
            if (check)
                newUnUsed.add(i);
        }
        return newUnUsed;
    }

    public LinkedList<Integer> getLayout() {
        return layout;
    }

    public boolean checkExist(int cellNumber) {
        for (int i = 0; i < 4; i++){
            if((layout.get(0) + i) == cellNumber) {
                shipsLives[0]--;
                return true;
            }
        }

        for (int i = 1; i < 3; i++){
            for (int j = 0; j <3; j++){
                if((layout.get(i) + j) == cellNumber) {
                    shipsLives[i]--;
                    return true;
                }
            }
        }

        for (int i = 3; i < 6; i++){
            for (int j = 0; j <2; j++){
                if((layout.get(i) + j) == cellNumber) {
                    shipsLives[i]--;
                    return true;
                }
            }
        }

        for (int i = 6; i < 10; i++){
            if(layout.get(i) == cellNumber) {
                shipsLives[i]--;
                return true;
            }
        }

        return false;
    }

    Integer checkShip_i_isDead(){
        for (int i = 0; i < 10; i++){
            if((shipsLives[i] == 0) && !shipDestroyed[i]){
                numberOfAliveShips--;
                shipDestroyed[i] = true;
                return (shipKind[i]*256 + layout.get(i));
            }
        }
        return -1;
    }

    boolean shipsEnd(){
        //System.out.println(numberOfAliveShips);
        return (numberOfAliveShips == 0);
    }
}
