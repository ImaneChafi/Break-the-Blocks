package ca.mcgill.ecse223.block.application;

import ca.mcgill.ecse223.block.model.*;
import ca.mcgill.ecse223.block.persistence.Block223Persistence;
import ca.mcgill.ecse223.block.view.Block223MainPage;

public class Block223Application {

    private static Block223 block223;
    private static UserRole currentUserRole;
    private static Game currentGame;
    private static GameSession currentGameSession;

    public static void main(String[] args) {
        // start UI
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Block223MainPage().setVisible(true);
            }
        });
    }

    public static Block223 resetBlock223() {
        // Method that forces a load from the file
        block223 = Block223Persistence.load();
	// Return the root Block223 object
	return block223;
    }
	
    public static Block223 getBlock223() {
        if (block223 == null) {
            resetBlock223();
        }
        return block223;
    }
    
    public static void setCurrentUserRole(UserRole aUserRole){
        currentUserRole = aUserRole;
    }
    
    public static UserRole getCurrentUserRole(){
        return currentUserRole;
    }
    
    public static void setCurrentGame(Game aGame){
        currentGame = aGame;
    }
    
    public static Game getCurrentGame(){
        return currentGame;
    }
    
    public static void setCurrentGameSession(GameSession aGameSession) {
    	currentGameSession = aGameSession;
    }
    
    private static GameSession getCurrentGameSession() {
    	return currentGameSession;
    }
}
