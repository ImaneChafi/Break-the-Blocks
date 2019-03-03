package ca.mcgill.ecse223.block.controller;

import java.util.ArrayList;
import java.util.List;
import ca.mcgill.ecse223.block.model.*;
import ca.mcgill.ecse223.block.application.*;
import ca.mcgill.ecse223.block.persistence.Block223Persistence;

import ca.mcgill.ecse223.block.application.*;
import ca.mcgill.ecse223.block.model.*;

public class Block223Controller {

    // ****************************
    // Modifier methods
    // ****************************
    /**
     * This method creates a new game within the Block223 Application
     * Author: Kelly Ma
     * @param name The unique name of the game
     * @throws InvalidInputException If the user is not an admin
     * @throws InvalidInputException If the name selected by the user is not
     * unique
     */
    public static void createGame(String name) throws InvalidInputException {

        // Verify that the user is an admin before proceeding
        if (!(Block223Application.getCurrentUserRole() instanceof Admin)) {
            throw new InvalidInputException("Admin privileges are required to create a game.");
        }

        // Get block223 and admin
        Block223 block223 = Block223Application.getBlock223();
        Admin admin = (Admin) Block223Application.getCurrentUserRole();

        // Create game and catch runtime exceptions
        // Exceptions are specified in the injected UMPLE code
        try {
            Game game = new Game(name, 1, admin, 1, 1, 1, 10, 10, block223);
            block223.addGame(game);
        } catch (RuntimeException e) {
            throw new InvalidInputException(e.getMessage());
        }

    }

    /**
     * This method defines game settings for a game in Block223
     * Author: Kelly Ma
     * @param nrLevels The number of levels available in the game
     * @param nrBlocksPerLevel The number of blocks per level in the game
     * @param minBallSpeedX The minimum ball speed in the x-direction
     * @param minBallSpeedY The minimum ball speed in the y-direction
     * @param ballSpeedIncreaseFactor The minimum factor by which ball speed
     * increases
     * @param maxPaddleLength The maximum length of the paddle
     * @param minPaddleLength The minimum length of the paddle
     * @throws InvalidInputException If the currentUserRole is not set to an
     * AdminRole
     * @throws InvalidInputException If the user is not the admin who created
     * the game
     * @throws InvalidInputException If a game is not selected to define game
     * settings
     * @throws InvalidInputException If the number of levels is not between [1,
     * 99]
     * @throws InvalidInputException If nrBlocksPerLevel is negative or zero
     * @throws InvalidInputException If minBallSpeedX is negative or zero
     * @throws InvalidInputException If minBallSpeedY is negative or zero
     * @throws InvalidInputException If ballSpeedIncreaseFactor is negative or
     * zero
     * @throws InvalidInputException If maxPaddleLength is negative or zero or
     * larger than the play area
     * @throws InvalidInputException If minPaddleLength is negative or zero
     */
    public static void setGameDetails(int nrLevels, int nrBlocksPerLevel, int minBallSpeedX, int minBallSpeedY,
            Double ballSpeedIncreaseFactor, int maxPaddleLength, int minPaddleLength) throws InvalidInputException {

        // Obtain the selected game
        Game game = Block223Application.getCurrentGame();

        // Verify that the user is an admin
        if (!(Block223Application.getCurrentUserRole() instanceof Admin)) {
            throw new InvalidInputException("Admin privileges are required to define game settings.");
        }

        // Verify that a game is selected 
        if (game == null) {
            throw new InvalidInputException("A game must be selected to define game settings.");
        }

        // Verify that the admin is the same user who created the game
        if (Block223Application.getCurrentUserRole() != game.getAdmin()) {
            throw new InvalidInputException("Only the admin who created the game can define its game settings.");
        }

        // Verify the nrLevels is between [1, 99]
        if (nrLevels < 1 || nrLevels > 99) {
            throw new InvalidInputException("The number of levels must be between 1 and 99.");
        }

        // Set nrBlocksPerLevel
        try {
            game.setNrBlocksPerLevel(nrBlocksPerLevel);
        } catch (RuntimeException e) {
            throw new InvalidInputException(e.getMessage());
        }

        // Obtain ball
        Ball ball = game.getBall();

        // Set minBallSpeedX
        try {
            ball.setMinBallSpeedX(minBallSpeedX);
        } catch (RuntimeException e) {
            throw new InvalidInputException(e.getMessage());
        }

        // Set minBallSpeedY
        try {
            ball.setMinBallSpeedY(minBallSpeedY);
        } catch (RuntimeException e) {
            throw new InvalidInputException(e.getMessage());
        }

        // Set ballSpeedIncreaseFactor
        try {
            ball.setBallSpeedIncreaseFactor(ballSpeedIncreaseFactor);
        } catch (RuntimeException e) {
            throw new InvalidInputException(e.getMessage());
        }

        // Obtain paddle
        Paddle paddle = game.getPaddle();

        // Set maxPaddleLength
        try {
            paddle.setMaxPaddleLength(maxPaddleLength);
        } catch (RuntimeException e) {
            throw new InvalidInputException(e.getMessage());
        }

        // Set minPaddleLength
        try {
            paddle.setMinPaddleLength(minPaddleLength);
        } catch (RuntimeException e) {
            throw new InvalidInputException(e.getMessage());
        }

        // Obtain the number of levels and size of list
        List<Level> levels = game.getLevels();
        int size = levels.size();


        // Modify nrLevels
        while (nrLevels > size) {
            game.addLevel();
            size = levels.size();
        }
        while (nrLevels < size) {
            Level level = game.getLevel(size - 1);
            level.delete();
            size = levels.size();
        }

    }

    /**
     * This method deletes a game. Author: Georges Mourant
     *
     * @param name name of the game
     * @throws InvalidInputException If the game does not exist
     * @throws InvalidInputException If the user is not an admin
     */
    public static void deleteGame(String name) throws InvalidInputException {
        Game foundGame = findGame(name);

        // error if not an Admin
        if (!(Block223Application.getCurrentUserRole() instanceof Admin)) {
            throw new InvalidInputException("Admin privileges are required to delete a game.");
        }

        Block223 block;

        // make sure the game exists
        if (foundGame != null) {
            // error if it's the wrong admin
            if (Block223Application.getCurrentUserRole() != foundGame.getAdmin()) {
                throw new InvalidInputException("Admin privileges are required to delete a game.");
            }

            // get Block223 so can save
            block = foundGame.getBlock223();

            // delete the game
            foundGame.delete();

            // save
            Block223Persistence.save(block);
        }
    }

    /**
     * This method takes finds a game, and sets it as the currently played game
     * in Block223Application. Authors: Georges Mourant & Kelly Ma
     *
     * @param name unique name of the game
     * @throws InvalidInputException If the game does not exist
     * @throws InvalidInputException If the user is not an admin
     * @throws InvalidInputException If the current admin is not the game
     * creator
     */
    public static void selectGame(String name) throws InvalidInputException {
        Game game = findGame(name);

        // error if game does not exist
        if (game == null) {
            throw new InvalidInputException("A game with the name " + name + " does not exist.");
        }
        // error if not an Admin
        if (!(Block223Application.getCurrentUserRole() instanceof Admin)) {
            throw new InvalidInputException("Admin privileges are required to delete a game.");
        }
        // error if it's the wrong admin
        if (Block223Application.getCurrentUserRole() != game.getAdmin()) {
            throw new InvalidInputException("Only the admin who created the game can delete the game.");
        }

        // If all else is good, select the game
        Block223Application.setCurrentGame(game);
    }

    /**
     * This method updates game information. Author: Georges Mourant
     *
     * @param name name of the game
     * @param nrLevels number of levels in the game
     * @param nrBlocksPerLevel number of blocks per level
     * @param minBallSpeedX minimum speed of the ball in X coordinates
     * @param minBallSpeedY minimum speed of the ball in Y coordinates
     * @param ballSpeedIncreaseFactor the increase factor of the ball's speed
     * @param maxPaddleLength Maximum length of the paddle
     * @param minPaddleLength Minimum length of the paddle
     * @throws ca.mcgill.ecse223.block.controller.InvalidInputException
     */
    public static void updateGame(String name, int nrLevels, int nrBlocksPerLevel, int minBallSpeedX, int minBallSpeedY,
            Double ballSpeedIncreaseFactor, int maxPaddleLength, int minPaddleLength) throws InvalidInputException {
        // getting current game's name
        Game game = Block223Application.getCurrentGame();
        String currentName = game.getName();

        // updating name
        if (currentName != name) {
            game.setName(name);
        }

        // updating all other information
        setGameDetails(nrLevels, nrBlocksPerLevel, minBallSpeedX, minBallSpeedY,
                ballSpeedIncreaseFactor, maxPaddleLength, minPaddleLength);
    }

    /**
     * This method creates a block in a game. Author: Imane Chafi
     *
     * @param RGB values
     * @param number of points
     * @throws InvalidInputException If the game is not selected
     * @throws InvalidInputException If the user is not an admin
     * @throws InvalidInputException If the user is not the admin who created the game
     * @throws InvalidInputException If the block used already exists
     * @throws InvalidInputException If the color values are not between 0 and 255
     * @throws InvalidInputException If the user if the points are not between 1 and 1000
     */
	public static void addBlock(int red, int green, int blue, int points) throws InvalidInputException {
		// Obtain the selected game
        Game game = Block223Application.getCurrentGame();
		String error = "";
		if(!(Block223Application.getCurrentUserRole() instanceof Admin)) {
			throw new InvalidInputException("Admin privileges are required to add a block.");
		}
		if(Block223Application.getCurrentGame() == null) {
			throw new InvalidInputException("A game must be selected to add a block");
		}
		if(Block223Application.getCurrentUserRole() != game.getAdmin()) {
			throw new InvalidInputException("Only the admin who created the game can add a block");
		}
		if(game.hasBlocks()) { //Question for teacher about getting the blocks with the same colors
			throw new InvalidInputException("A block with the same color already exists for the game");
		}
		try {
			game.addBlock(red, green, blue, points); //Can I do it like this instead of "create(..)"?
		}
		catch (RuntimeException e) { //Do I need to make catch and rethrow statements individually?
			error = e.getMessage();
			if ((red < 0) || (red > 255))
				throw new InvalidInputException("Red must be between 0 and 255.");
			
			if ((green < 0) || (green > 255))
				throw new InvalidInputException("Green must be between 0 and 255.");
			
			if ((blue < 0) || (blue > 255))
				throw new InvalidInputException("Blue must be between 0 and 255.");
			
			if ((points < 1) || (red > 1000))
				throw new InvalidInputException("Points must be between 1 and 1000.");
		}
	}
/**
     * This method deletes a block from a game. Author: Imane Chafi
     *
     * @param id of block to be deleted
     * @throws InvalidInputException If the game is not selected
     * @throws InvalidInputException If the user is not an admin
     * @throws InvalidInputException If the user is not the admin who created the game
     * 
     * */
	public static void deleteBlock(int id) throws InvalidInputException {
		Game game = Block223Application.getCurrentGame();
		if(!(Block223Application.getCurrentUserRole() instanceof Admin)) {
			throw new InvalidInputException("Admin privileges are required to delete a block.");
		}
		if(Block223Application.getCurrentGame() == null) {
			throw new InvalidInputException("A game must be selected to delete a block");
		}
		if(Block223Application.getCurrentUserRole() != game.getAdmin()) {
			throw new InvalidInputException("Only the admin who created the block can delete the block");
		}
		
		Block block = findBlock(id); //Go to find block method for the declaration
		if(block != null)
			block.delete();
	}

    /**
     *
     * This method updates a block with new values. It requires a block ID,
     * color values (RGB) and the point value of the block.
     * 
     * @author Mathieu Bissonnette
     *
     * @param id	The ID of the desired block.
     * @param red	The red component of the block color.
     * @param green	The green component of the block color.
     * @param blue	The blue component of the block color.
     * @param points	The point value of the block.
     *
     * @throws InvalidInputException	if red, green, or blue < 0 or > 255 or if
     *                                  points < 0 or > 1000
     * @throws InvalidInputException	if the block ID does not correspond to an
     *                                  existing entity.
     */
    public static void updateBlock(int id, int red, int green, int blue, int points) throws InvalidInputException {
    	
        // Verify that the user is an admin before proceeding.
    	
        if (!(Block223Application.getCurrentUserRole() instanceof Admin)) {
            throw new InvalidInputException("Admin privileges are required to update a block.");
        }
    	
        // Perform basic input validation to ensure the numeric values are valid.
        
        if (red > 255 || red < 0) {
            throw new InvalidInputException("Red value not valid");
        } else if (green > 255 || green < 0) {
            throw new InvalidInputException("Green value not valid");
        } else if (blue > 255 || blue < 0) {
            throw new InvalidInputException("Blue value not valid");
        } else if (points > 1000 || points < 1) {
            throw new InvalidInputException("Point value not valid");
        }

        // Get the block list for the selected game.
        
        Game game = Block223Application.getCurrentGame();
        if (game == null) {
            throw new InvalidInputException("No game selected");
        }
        List<Block> blocks = game.getBlocks();

        // Find the desired block in the block list.
        
        Block foundBlock = null;
        for (Block block : blocks) {
            int blockID = block.getId();
            if (blockID == id) {
                foundBlock = block;
                break;
            }
        }

        if (foundBlock == null) {
            throw new InvalidInputException("Invalid block ID");
        }

        // Update block data
        
        foundBlock.setRed(red);
        foundBlock.setGreen(green);
        foundBlock.setBlue(blue);
        foundBlock.setPoints(points);

    }

    /**
    *
    * This method assigns a block to a position in a game's level. It needs
    * a level index, a block ID and a x/y grid position.
    *
    * @param id    The ID of the desired block.
    * @param level The index of the desired level.
    * @param gridHorizontalPosition        The grid horizontal position where the block will be positioned.
    * @param gridVerticalPosition          The grid vertical position where the block will be positioned.
    *
    * @throws InvalidInputException        if the level index is < 0 or > 98.
    * @throws InvalidInputException        if the level index or the block ID
    *                                 	   do not correspond to an existing
    *                                      entity.
    *
    */

    public static void positionBlock(int id, int level, int gridHorizontalPosition, int gridVerticalPosition) throws InvalidInputException {
		
		// Verify that the user is an admin before proceeding.
    	
		if (!(Block223Application.getCurrentUserRole() instanceof Admin)) {
		    throw new InvalidInputException("Admin privileges are required to create a game.");
		}
			  
		// Perform basic input validation to ensure the numeric values are valid.
		
		if (level > 98 || level < 0) {
			throw new InvalidInputException("Level index not valid");
		}

		// Get the block list for the selected game.
		
		Game game = Block223Application.getCurrentGame();
		if (game == null) {
			throw new InvalidInputException("No game selected");
		}
		
		// Get the desired level.
		
		Level foundLevel = game.getLevel(level);
		if (foundLevel == null) {
			throw new InvalidInputException("Level not found");
		}
		
		// Get the block list from the game.
		
		List<Block> blocks = game.getBlocks();
		
		// Find the desired block in the block list.
		
		Block foundBlock = null;
		for (Block block : blocks) {
			int blockID = block.getId();
	    	if (blockID == id) {
				foundBlock = block;
				break;
		    }
	  	}
		if (foundBlock == null) {
	    throw new InvalidInputException("Invalid block ID");
		}
		
		// Delete the block assignment at xy coords if it exists.
		
		List<BlockAssignment> assignments = foundLevel.getBlockAssignments();
		  	for (BlockAssignment block : assignments) {
		  		int x = block.getGridHorizontalPosition();
		  		int y = block.getGridVerticalPosition();
		  		if (x == gridHorizontalPosition && y == gridVerticalPosition) {
		  			block.delete();
		  		}
		  	}
		
	  	// Create a new BlockAssignment.
		  	
	  	foundLevel.addBlockAssignment(gridHorizontalPosition, gridVerticalPosition, foundBlock, game);

    }

    public static void moveBlock(int level, int oldGridHorizontalPosition, int oldGridVerticalPosition,
            int newGridHorizontalPosition, int newGridVerticalPosition) throws InvalidInputException {
    }

    public static void removeBlock(int level, int gridHorizontalPosition, int gridVerticalPosition)
            throws InvalidInputException {
    }

    public static void saveGame() throws InvalidInputException {
    }

    public static void register(String username, String playerPassword, String adminPassword)
            throws InvalidInputException {
    }

    public static void login(String username, String password) throws InvalidInputException {
    }

    public static void logout() {
    }

    // ****************************
    // Query methods
    // ****************************
    /**
     * This method returns a list of designable games for the current admin.
     * Author: Georges Mourant
     *
     * @return list of designable games
     * @throws InvalidInputException
     */
    public static List<TOGame> getDesignableGames() throws InvalidInputException {
        // get the Block223
        Block223 block = Block223Application.getBlock223();

        // error if not an Admin, save Admin
        Admin admin; // holder
        if (Block223Application.getCurrentUserRole() instanceof Admin) {
            admin = (Admin) Block223Application.getCurrentUserRole(); // set val
        } else { // throw error
            throw new InvalidInputException("Admin privileges are required to access game information.");
        }

        // create transfer object list
        List<TOGame> result = new ArrayList();

        // get games list
        List<Game> games = block.getGames();

        for (Game game : games) {
            // get game admin
            Admin gameAdmin = game.getAdmin();
            // if current admin is game admin, allow game to be added to list
            if (gameAdmin.equals(admin)) {
                TOGame to = new TOGame(game.getName(), game.numberOfLevels(),
                        game.getNrBlocksPerLevel(), game.getBall().getMinBallSpeedX(),
                        game.getBall().getMinBallSpeedY(), game.getBall().getBallSpeedIncreaseFactor(),
                        game.getPaddle().getMaxPaddleLength(), game.getPaddle().getMinPaddleLength());
                result.add(to);
            }
        }
        return result; // return result
    }

    /**
     * Returns the transfer object of a game. Author: Georges Mourant
     *
     * @return the currently played game
     * @throws InvalidInputException If the user is not an admin
     */
    public static TOGame getCurrentDesignableGame() throws InvalidInputException {
        // get current game
        Game game = Block223Application.getCurrentGame();
        // return game as transfer object
        return new TOGame(game.getName(), game.numberOfLevels(),
                game.getNrBlocksPerLevel(), game.getBall().getMinBallSpeedX(),
                game.getBall().getMinBallSpeedY(), game.getBall().getBallSpeedIncreaseFactor(),
                game.getPaddle().getMaxPaddleLength(), game.getPaddle().getMinPaddleLength());
    }

    public static List<TOBlock> getBlocksOfCurrentDesignableGame() throws InvalidInputException {
		Game game = Block223Application.getCurrentGame();
		if(!(Block223Application.getCurrentUserRole() instanceof Admin))
			throw new InvalidInputException("Admin privileges are required to access game information.");
		if(Block223Application.getCurrentGame() == null)
			throw new InvalidInputException("A game must be selected to access its information");
		if(Block223Application.getCurrentUserRole() != game.getAdmin())
			throw new InvalidInputException("Only the admin who created the game can acess its information");
		
		
		List<TOBlock> result = new ArrayList<TOBlock>();
		
		for (Block block : game.getBlocks()) {
			TOBlock to = new TOBlock(block.getId(), block.getRed(), block.getGreen(), block.getBlue(), block.getPoints());
			result.add(to);}
		return result;
		}

    /**
    *
    * This method returns a list of GridCells associated to a level.
    * It needs a level index.
    * 
    * @author Mathieu Bissonnette
    *
    * @param level The index of the desired level.
    *
    * @return A list of the GridCells transfer objects associated to a level.
    *
    * @throws InvalidInputException        if the level doesn't exists.
    *
    */

    public List<TOGridCell> getBlocksAtLevelOfCurrentDesignableGame(int level) throws InvalidInputException {

    	// Perform basic input validation to ensure the numeric values are valid.
	
    	if (level > 98 || level < 0) {
    		throw new InvalidInputException("Level index not valid");
    	}
	
    	// Get the desired level from the current game.
	
    	Game game = Block223Application.getCurrentGame();
    	Level foundLevel = game.getLevel(level);
    	if (foundLevel == null) {
    		throw new InvalidInputException("Level not found");
    	}
	
    	// Get the list of block assignments of the level
	
    	List<BlockAssignment> assignments = foundLevel.getBlockAssignments();
	  
    	// Create a list of TOGridCell objects and populate it.
		
    	List<TOGridCell> result = new ArrayList<TOGridCell>();
    	for (BlockAssignment assignment : assignments) {
		  Block block = assignment.getBlock();
		  TOGridCell cell = new TOGridCell(	assignment.getGridHorizontalPosition(), 
											assignment.getGridVerticalPosition(), 
											block.getId(), 
											block.getRed(), 
											block.getGreen(), 
											block.getBlue(), 
											block.getPoints() );
		  result.add(cell);
    	}
	
    	// Return the result.
	
    	return result;
	
    }

    // ****************************
    // Private Helper Methods
    // ****************************
    /**
     * This method does what Umple's Game.getWithName(…) method would do if it
     * worked properly aka get the game using the name. Authors: Georges Mourant
     * & Kelly Ma
     */
    private static Game findGame(String name) {
        Game foundGame = null;
        for (Game game : Block223Application.getBlock223().getGames()) {
            if (game.getName().equals(name)) {
                foundGame = game;
                break;
            }
        }
        return foundGame;
    }
/**
 * This method finds a block inside a list of blocks depending on its
 * ID. Author : Imane Chafi */

public static Block findBlock(int id) { //Here, this is how the method was written in the solution document. 
	//I've emailed the teacher about this to have clarification, and whether the "." is necessary. 
	Game game = Block223Application.getCurrentGame();
	List<Block> blocks = game.getBlocks();//Here, I would need to get the current game first, put I need to ask the teacher about the Game.find to understand what it means.
	
	for (Block block : blocks) {
		
		int blockId = block.getId(); //Here, the type of blockID should be integer
		if(id == blockId)
			return block;
	}
	return null;	
}
}
