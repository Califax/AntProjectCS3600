#!/usr/bin/env python
import random 
import Ant
from Ant import *
from random import shuffle
from ants import *
from DataStructs import *
import DataStructs
import logging
#from logutils import initLogging,getLogger
#import gc
# define a class with a do_turn method
# the Ants.run method will parse and update bot input
# it will also run the do_turn method for us
class MyBot:
    def __init__(self):
        # define class level variables, will be remembered between turn
       self.enemyList = [] # List of enemy ants 
       self.foodList = []
       self.myHill = []
       self.antLocations = [] # Store Ant Object locations
       self.antList = []
       self.targets = {} # Key = target location, value = antObj
       self.targeted = [] # list of locations ants will move next turn
       self.directionList = ['s','e','w','n']
       self.nextLocList = [] # List of locations where ants are expected to move
       self.turnNumber = 0
       self.ordersToDelete = []
       self.orders = []
       self.enemyAntList = []
       self.levels = 500 # Used to store how many levels an ant searches
       self.antDict = {}
       self.enemyCloseToHill = []
       self.friendyCloseToHill = []
    # do_setup is run once at the start of the game
    # after the bot has received the game settings
    # the ants class is created and setup by the Ants.run method
    def do_setup(self, ants):
        # initialize data structures after learning the game settings
        self.hills = []
        self.visitedList = []
        self.counter = 0 # Used to enumerate antObjs
        #self.ordersToDelete = []
        #ant = Ant(self.counter, ants.my_ants()) # Create initial ant 
        #self.antList.append(ant) # Put initial ant in the antList
        self.foodList = ants.food()
        self.attackRadius = ants.getAttackRadius()
        logging.basicConfig(filename='example.log',level=logging.DEBUG)

    # do turn is run once per turn
    # the ants class has the game state and is updated by the Ants.run method
    # it also has several helper methods to use
    def removeTargets(self, loc):
            #self.targets[loc] == None 
            del self.targets[loc]             
    
    def aStarFromHill(self, ants, myHillLoc):
        visitedList = []
        level = 0
        myPQ = DataStructs.PriorityQueue()
        myPQ.push((myHillLoc, 0), 0)
        enemyList = []
        friendlyList = []
        while not myPQ.isEmpty() and level < 155:
            level += 1
            currNode = myPQ.pop()
             
            if (currNode[0], 1) in self.enemyList: # Found Enemy Ant
                 enemyList.append(currNode[0])
            if (currNode[0] in self.antList): # Found Friendly Ant
                 friendlyList.append(currNode[0])
             
            if currNode[0] not in visitedList:
                visitedList.append(currNode[0])
                for direction in ('s','e','w','n'):
                    new_loc = ants.destination(currNode[0], direction)
                    dist = ants.distance(currNode[0], new_loc)
                    if new_loc not in visitedList and ants.passable(new_loc):
                        myPQ.push((new_loc, dist), dist) # Should new_loc be a tuple or not AND SHOULD I ADD IF IT IS WATER IN THIS CASE?
        
        return enemyList, friendlyList
    
                
    def aStar(self, ants, antObj):
            visitedList = []
            unvisableDistList = []
            foodDistList = []
            level = 0
            foodDisc = []
            friendlyList = []
            parent = None # First node doesn't have a parent
            direct = None # First node doesn't have a parent
            pathCost = 0 # start with 0 pathCost
            myPQ = DataStructs.PriorityQueue()
            myPQ.push((antObj.getLocation(), parent, direct, antObj, pathCost), pathCost)
           # myPQ.push((antObj.getLocation(), parent, direct, antObj), pathCost)
            directions = []
            minDist = []
            #minDist.append(5)
            hills = []
            while not myPQ.isEmpty() and level < self.levels:
                level += 1
                #if ants.time_remaining() < 100:
                 #  ants.finish_turn()
               # shuffle(directionList)
                currNode = myPQ.pop()
                
                if (currNode[0] in self.antList): # Found Friendly Ant
                    friendlyList.append(self.antDict[currNode[0]])
                """
                if (currNode[0], 1) in self.enemyList:
                    if currNode[0] not in antObj.getEnemyList(): 
                        antObj.addEnemyList(currNode[0])
                """
                if currNode[0] in self.hills and currNode[0] not in self.targets.keys():# and currNode[0] not in self.targeted: # Food isn't being targeted
                    bestPath = self.returnPath(currNode)
                    #minDist.append(ants.distance(currNode[3].getLocation(), currNode[0]))
                    hills.append((len(bestPath), currNode[0], currNode[3], bestPath))
                   # minDist.append(0) # hills most important
                    
                if currNode[0] in self.foodList and currNode[0] not in self.targets.keys():# and currNode[0] not in self.targeted: # Food isn't being targeted
                    bestPath = self.returnPath(currNode)
                    if len(bestPath) > 1: 
                        bestPath.pop() # Remove last direction as food is gathered if you move next to it
                    #minDist.append(ants.distance(currNode[3].getLocation(), currNode[0]))
                    #minDist.append(1) # food is important so good heuristic for it
                    foodDistList.append((len(bestPath), currNode[0], currNode[3], bestPath)) # (ants.distance(currNode[0], currNode[3].getLocation()

                if not ants.visible(currNode[0]) and currNode[0] not in self.targets.keys(): # and currNode[0] not in self.targeted:  # Check if current state is visible - this is a goal state               
                   bestPath = self.returnPath(currNode)
                   unvisableDistList.append((len(bestPath), currNode[0], currNode[3], bestPath)) # Store path to node and the location
   
                if currNode[0] not in visitedList:
                   # logging.debug("visited list: %s"% (str(visitedList)))
                    visitedList.append(currNode[0])
                    for direction in ('s','e','w','n'):
                        new_loc = ants.destination(currNode[0], direction)
                       # dist = ants.manhattanDistance(currNode[3].getLocation(), new_loc)
                        dist = ants.distance(currNode[3].getLocation(), new_loc) # distance from ant to new location
                        if ants.passable(new_loc) and new_loc not in visitedList and new_loc not in self.myHill: 
                            myPQ.push ((new_loc, currNode, direction, currNode[3], dist + currNode[4]), dist + currNode[4])            
                            #myPQ.push ((new_loc, currNode, direction, currNode[3]), dist) # currNode[3] is the antObj
           # return (foodDistList, unvisableDistList)
          #  return (foodDistList, unvisableDistList)
            # A Star searching is done, now sort results

            if len(hills) > 0:
             hills.sort()
             for lengthPath, dest, newAntObj, bestPath in hills:
                #if dest in self.targets.keys() or dest in self.targeted:
                 #   continue
               # else: 
                    antObj.giveOrders((dest, bestPath))
                    # CURRENTLY COMMENTED BELOW AS WANT A LOT OF ANTS TO GO TOWARDS HILL
                    self.targets[dest] = antObj # Targets: Key = targeted location value = Ant Object with given target destination
                    #self.move_ant(ants, antObj)
                    return

            if len(foodDistList) > 0:    
             foodDistList.sort() # Sort by shortest direction path
             for lengthPath, dest, newAntObj, bestPath in foodDistList:
                if dest in self.targets.keys():
                    continue
                else:
                    antObj.giveOrders((dest, bestPath))
                    self.targets[dest] = antObj # Targets: Key = targeted location value = Ant Object with given target destination
                    #self.move_ant(ants, antObj)
                    return
            
            
            if len(unvisableDistList) > 0: 
             unvisableDistList.sort() # Sort by shortest number of directions in  path
             for lengthPath, dest, newAntObj, bestPath in unvisableDistList:
                if dest in self.targets.keys():
                    continue
                else:
                    antObj.giveOrders((dest, bestPath))
                    self.targets[dest] = antObj
                   # self.move_ant(ants, antObj)
                    return
            
           
    def returnPath(self, currNode):
            directions = []
            parentNode = currNode 
            if (parentNode[2] != None):
                directions.append(parentNode[2])
            while parentNode[1] != None:
                    parentNode = parentNode[1]
                    if parentNode[1] != None:
                        directions.append(parentNode[2])
            directions.reverse()
            return directions
        
        # prevent stepping on own hill
    def avoidOwnHill(self, ants):
            if len(ants.my_ants()) > 1:
                for hill_loc in ants.my_hills():
                    orders[hill_loc] = None # So no ants are ordered to step on the hill
            
        
        # Update visible foodList will discard all previously visible food wether it was collected or not
    def updateFoodList(self, ants):
            self.foodList = []
            for food in ants.food():
                if food not in self.foodList:
                    self.foodList.append(food) # CHECK WITH UPDATES ants

        # unblock own hill
    def unblockHill(self, ants):
            #if len(self.myHill) < 1: # We don't have a hill
               # return 
            for antObj in self.antList:
               # if antObj.hasOrders():
                #   continue
                if antObj.getLocation() in self.myHill: # Check if my hill is the object location
                            for direction in self.directionList:
                                dir = []
                                new_loc = ants.destination(antObj.getLocation(), direction) # get new location given cur loc and direction to move
                                if ants.unoccupied(new_loc) and ants.passable(new_loc) and new_loc not in self.targeted:
                                    #ants.issue_order((antObj.getLocation(), direction))
                                    dir.append(direction)  # Because giveOrders expects list of directions
                                    #logging.debug("dir in unblockHill: %s"% (str(dir)))
                                    antObj.giveOrders((new_loc, dir))
                                    self.targets[new_loc] = antObj
                                    #antObj.setLocation(new_loc)
                                    self.antList.remove(antObj)
                                    self.antList.insert(0, antObj)
                                    #self.targeted.append(new_loc)
                                    break      
                else:
                    continue
    def chargeEnemyHill(self, ants):
        for antObj in self.antList:
            ant_loc = antObj.getLocation()
            if ants.time_remaining() < 50:
                return
            if antObj.hasOrders() or antObj.getMoved() == True:
                continue
            if len(self.hills) == 0:
                return # No hill to charge
            
            dirList = ants.direction(antObj.getLocation(), self.hills[len(self.hills) - 1]) # CHECK
            if len(dirList) != 0:
                    for direction in dirList:
                        new_loc = ants.destination(antObj.getLocation(), direction)
                        if ants.unoccupied(new_loc) and ants.passable(new_loc) and new_loc not in self.targeted:
                            antObj.setMoved(True)
                            antObj.giveOrders((None, None)) # Only 1 move
                            antObj.setLocation(new_loc)
                            self.targeted.append(new_loc)
                            ants.issue_order((ant_loc, direction))
                            break
            
    def randomMoves2(self, ants):
            for antObj in self.antList:
                if ants.time_remaining() < 50:
                    return
                if antObj.hasOrders() or antObj.getMoved() == True:
                    continue
                dir = []
                dirFinal = []
                maxDist = 0
                bestDir = 'n'
                shuffle(self.directionList)
                for direction in self.directionList: #('s','e','w','n'):
                    dist = ants.distance(antObj.getLocation(), self.myHill[0])
                    new_loc = ants.destination(antObj.getLocation(), direction) # get new location given current loc and direction to move
                    distNew = ants.distance(new_loc, self.myHill[0])
                    #if dist > distNew and distNew < 10: # Moving closer to hill, don't want this
                     # continue
                    if distNew > dist and ants.unoccupied(new_loc) and ants.passable(new_loc) and new_loc not in self.targeted and new_loc not in self.targets.keys(): #and new_loc not in self.targeted:
                        dir.append((direction, distNew, new_loc))
                        #bestDist.append(dist)
               # logging.debug("dir in randomMoves: %s"% (str(dir)))
                if len(dir) == 0: # No legal moves
#                    antObj.targDest = None # CHECK
                    continue
                else:
                    shuffle(dir)
                    dirFinal.append(dir[0][0])  
                    logging.debug("dirFinal %s"% (str(dirFinal)))  
                    best_loc = dir[0][2] # new location 
                    antObj.giveOrders((best_loc, dirFinal)) # dirFinal must be a list of directions
                    #self.targets[best_loc] = antObj
                        
    def ant_locations(self, ants): # Store all antObj locations in ant_locations
            for antObj in self.antList:
                antObj.setMoved(False) # No ants have moved this turn
                loc = antObj.getLocation()
                self.antDict[loc] = antObj # key = location value = ant object
                if loc not in self.antLocations:
                    self.antLocations.append(loc)
                    
    
    def updateDeadAnts(self, ants): 
        deadList = ants.getDeadList()
        for antObj in self.antList[:]: # Remove dead ants from ant list
                antLoc = antObj.getLocation()
                if antLoc in deadList:
                    if antObj.hasOrders():
                        if antObj.getDest() in self.targets:
                            self.removeTargets(antObj.getDest())
                    if antObj.getLocation() in self.antLocations: 
                        self.antLocations.remove(antObj.getLocation()) 
                    if antLoc in self.antDict.keys():
                        del self.antDict[antLoc]
                    self.antList.remove(antObj)
                   
                      
    def updateAnts(self, ants):
            if len(ants.my_ants()) > len(self.antLocations):
                for ant_loc in ants.my_ants(): # my_ants() returns a list of all ant locations
                    if ant_loc not in self.antLocations:
                        ant = Ant(self.counter, ant_loc) # Create new ant because found food
                        self.antDict[ant_loc] = ant # Update dictionary to include new ant 
                        self.antList.append(ant)
                        self.antLocations.append(ant_loc)
                        self.counter += 1 # Increase the ant counter
                        
                               
    def move_ant(self, ants, antObj):
        if not antObj.hasOrders(): # Ant doesn't have orders
           return False
       
        if antObj.hasOrders():          
            dir = antObj.executeOrders(ants, self, antObj)

            if dir == False:
                return

            ant_loc = antObj.getLocation()
            new_loc = ants.destination(ant_loc, dir)
                    
            if ants.unoccupied(new_loc) and ants.passable(new_loc): # and new_loc not in self.targeted: # and new_loc not in self.targeted: # If the square is not water or occupied
                 antObj.setLocation(new_loc) # Save new location of the Ant Object
                 self.targeted.append(new_loc)
                 antObj.moved == True
                 #self.orders.append((ant_loc, dir))
                 ants.issue_order((ant_loc, dir))
          #  return True        
        
                   
    def move_ants(self, ants):
            #dir, dest = order
            for antObj in self.antList:
                if ants.time_remaining() < 25:
                    return
                if antObj.getMoved() == True:
                   continue
                #logging.debug("Time Remaining during move_ants(): %s"% (str(ants.time_remaining())))
                #logging.debug("Ant Num: %s"% (str(antObj.getAntNum())))
                if antObj.hasOrders():
                    dir = antObj.executeOrders(ants, self, antObj)
                    if dir == False:
                        #logging.debug("No Direction Returned Ant Num: %s"% (str(antObj.getAntNum())))
                        continue
                    #logging.debug("Direction %s"% (str(dir)))
                    ant_loc = antObj.getLocation()
                    new_loc = ants.destination(ant_loc, dir)
                    
                    if ants.unoccupied(new_loc) and ants.passable(new_loc) and new_loc not in self.targeted: # If the square is not water or occupied
                        antObj.setLocation(new_loc) # Save new location of the Ant Object
                       #self.targets[new_loc] = antObj # new_loc is now targeted
                       # del self.antDict[ant_loc] # Remove old location from dictionary
                       # self.antDict[new_loc] = antObj # update antDict for moved Ant
                        self.targeted.append(new_loc)
                        
                        #antObj.clearEnemyList()
                        antObj.setMoved(True)
                        ants.issue_order((ant_loc, dir))
                    
    def updateHills(self, ants):
        self.hills = []
        for hill_loc, hill_owner in ants.enemy_hills():
                if hill_loc not in self.hills: # Hill has yet to be discovered in previous turns
                    self.hills.append(hill_loc)
        for hill_loc in ants.my_hills(): # For each hill we own
               # if hill_loc in self.antLocations: # and hill_loc not in orders.values():
                    if hill_loc not in self.myHill:
                        self.myHill.append(hill_loc)  
    
    def deleteOrders(self, ants):
        for antObj in self.ordersToDelete:
            antObj.giveOrders((None, None)) 
            antObj.moved == False # Clear that is has moved
            if antObj.getDest() not in self.hills: 
                del self.targets[antObj.getDest()]  # Multiple ants can target hill
                            
    def do_turn(self, ants):
            self.antDict.clear() # Empty ant dictionary
            #self.antDict = {}
            self.enemyList = ants.enemy_ants()
            self.targeted = [] # Clear targeted (one move ahead targets)
            self.turnNumber += 1
            self.ordersToDelete = []
            logging.debug("Turn Number: %s"% (str(self.turnNumber)))
            self.antLocations = []
            self.antlocations = self.ant_locations(ants) # Fill ant locations array
            logging.debug("Time Remaining before updating dead ants: %s"% (str(ants.time_remaining())))
            self.updateDeadAnts(ants) # Remove dead ants removing corresponding antObjs 
            logging.debug("Time Remaining after updating dead ants: %s"% (str(ants.time_remaining())))
            self.updateAnts(ants) # Create new ant objects 
            self.updateFoodList(ants) # Update visible foodList
            self.updateHills(ants) # Update known hill locations, both enemy and own hills
            
            self.orders = []
            
            #self.targets[self.myHill[0]] = None
            # Call aStarFromHill for defense 
            self.enemyCloseToHill, self.friendlyCloseToHill = self.aStarFromHill(ants, self.myHill[0])
            
            if len(self.enemyCloseToHill) > 0 and len(self.friendlyCloseToHill) > 0:
                firstAntObj = self.antDict[self.friendlyCloseToHill[0]] # First item in list
                ant_loc = firstAntObj.getLocation()
                dirList = ants.direction(firstAntObj.getLocation(), myHill[0])
                if len(dirList) != 0:
                    for direction in dirList:
                        new_loc = ants.destination(ant_loc, direction)
                        if ants.unoccupied(new_loc) and ants.passable(new_loc) and new_loc not in self.targeted:
                            firstAntObj.giveOrders((None, None)) # Only 1 move so no orders
                            firstAntObj.setMoved(True)
                            firstAntObj.setLocation(new_loc)
                            self.targeted.append(new_loc)
                            ants.issue_order((ant_loc, direction)) # Move first Ant
                            break    
                                   
                for closeAnt in range(1, len(self.friendlyCloseToHill)): # For every antObj found in aStarHillSearch starting with second ant
                    closestEnemyAnt = None
                    friendlyAnt = self.antDict[closeAnt]
                    friendly_loc = friendlyAnt.getLocation() # Could just be closeAnt
                    minDist = 9999
                    for enemyLoc in self.enemyCloseToHill:
                        enemyAnt = self.antDict[enemyLoc]
                        dist = ants.distance(enemyLoc, friendly_loc)
                        if dist < minDist:
                            minDist = dist
                            closestEnemyAnt = enemyAnt # Store closest enemy ant thus far
                    
                    if closestEnemyAnt == None: # CHECK
                        continue        
                    dirList = ants.direction(friendlyAnt.getLocation(), closestEnemyAnt.getLocation())        
                    if len(dirList) != 0:
                        for direction in dirList:
                            new_loc = ants.destination(friendly_loc, direction)
                            if ants.unoccupied(new_loc) and ants.passable(new_loc) and new_loc not in self.targeted:
                                friendlyAnt.giveOrders((None, None)) # Only 1 move
                                friendlyAnt.setMoved(True)
                                friendlyAnt.setLocation(new_loc)
                                self.targeted.append(new_loc)
                                ants.issue_order((friendly_loc, direction)) # Move first Ant 
                                break   # Want to break out direction for loop, if this is last iteration will it still work as intended?
                                #continue
                    #self.move_ant(ants, self.antDict[closeAnts]) # key = location of ant value = ant obj
                    
                    
           # logging.warning("Ant List Locations: %s"% (str(locList)))
           # logging.warning("Length Ant List: %s"% (str(len(self.antList)))) 
                
           # self.targeted.append(self.myHill[0]) # Don't move to hill
           # logging.debug("Time Remaining before searching: %s"% (str(ants.time_remaining())))
            
            #for antObj in self.antList:
               # antObj.setHillDist = ants.distance(antObj.getLocation(), self.myHill[0])
                
            #self.antList = sorted(self.antList, key=lambda Ant: Ant.getHillDist())   # Sort by hill distance  
            #self.antList.reverse()
               
            #self.antList.sort(cmp=None, key=None, reverse=False)
            #logging.warning("Ant List Before Sort: %s"% (str(self.antList)))
            #self.antList = sorted(self.antList, key=lambda Ant: ants.distance(Ant.getLocation(), self.myHill[0]))   # sort by antNum
            #self.antList = sorted(self.antList, key=lambda Ant: Ant.getAntNum())   # sort by antNum
            #self.antList.reverse()
            #logging.warning("Ant List After appending: %s"% (str(self.antList)))
           # logging.warning("Enemy Ant Locations: %s"% (str(self.enemyList)))
            
            if len(self.antList) > 50: # So many ants need to reduce search radius for aStar
                self.levels = 400
            else:
                self.levels = 500
                
            if len(self.antList) > 5:
                self.chargeEnemyHill(ants)
            #shuffle(self.antList)
            logging.debug("Time Remaining after appending: %s"% (str(ants.time_remaining())))
            for antObj in self.antList:
               # logging.warning("Ant Locations: %s"% (str(antObj.getLocation())))
                if ants.time_remaining() < 50:
                    return
                if antObj.hasOrders() or antObj.getMoved() == True: # Don't call A Star on ants with a path or that have already moved
                    continue
                else:
                    self.aStar(ants, antObj)
                
            logging.debug("Time Remaining After Move: %s"% (str(ants.time_remaining())))
            
            #self.unblockHill(ants)       
            logging.debug("Time Remaining before doing randomMoves2() and after aStar: %s"% (str(ants.time_remaining())))
            self.randomMoves2(ants) 
            logging.debug("Time Remaining before doing move_ants(): %s"% (str(ants.time_remaining())))
            self.move_ants(ants)
            logging.debug("Time Remaining after doing move_ants(): %s"% (str(ants.time_remaining())))
            self.deleteOrders(ants)
            logging.debug("Time Remaining after doing deleteOrders(): %s"% (str(ants.time_remaining())))
            #logging.debug("Is passable (26,4): %s"% (str(ants.passable((26,4)))))
            #logging.debug("Ant Dict: %s"% (str(self.antDict)))
          #  return
           # logging.debug("ant hill[0]: %s"% (str(self.myHill[0])))
            #logging.debug("Targets: %s"% (str(self.targets.keys())))  
           # logging.debug("Target List: %s"% (str(self.targets.keys())))
            #self.deleteTargets()
            #logging.debug("Food List: %s"% (str(self.foodList)))
           # logging.warning("Targeted: %s"% (str(self.targeted)))
           # for antObj in self.antList:     
              # antObj.setMoved(False) # Ant hasn't moved yet
            #logging.warning("In Orders: %s"% (str(self.orders)))
               
if __name__ == '__main__':
    # psyco will speed up python a little, but is not needed
    try:
        import psyco
        psyco.full()
    except ImportError:
        pass
    
    try:
        # if run is passed a class with a do_turn method, it will do the work
        # this is not needed, in which case you will need to write your own
        # parsing function and your own game state class
        Ants.run(MyBot())
    except KeyboardInterrupt:
        print('ctrl-c, leaving ...')
