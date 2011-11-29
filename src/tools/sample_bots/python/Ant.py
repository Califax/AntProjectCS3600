import ants
from ants import *
import logging
import MyBot
from MyBot import *
import random
from random import shuffle
from logutils import initLogging,getLogger

class Ant():
     def __init__(self, num, loc):
        self.col = None
        self.row = None
        self.loc = loc
        self.orderList = (None, None)
        self.num = num
        self.directionList = ['s','e','w','n']
        self.targDest = None
        self.turnDelay = 0
        self.moved = False # True if Ant took a turn
        self.hillDist = 0 # Distance from own hill
        self.objEnemyList = []
        self.friendlyList = []
        logging.basicConfig(filename='example.log',level=logging.DEBUG)
     
     def getEnemyList(self):
         return self.objEnemyList
     
     def addEnemyList(self, loc):
         self.objEnemyList.append(loc)
    
     def addFriendlyList(self, antObj):
         self.friendlyList.append(antObj)
     
     def clearEnemyList(self):
         self.objEnemyList = []
     
     def getHillDist(self):
         return self.hillDist
     
     def setHillDist(self, dist):
         self.hillDist = dist
         
     def setMoved(self, bool):
         self.moved = bool
     
     def getMoved(self):
         return self.moved
     
     def getDest(self):
         return self.targDest
     
     def getLocation(self):
         return self.loc
     
     def setLocation(self, loc):
         self.loc = loc
    
     def giveOrders(self, (dest, directions)):
         self.orderList = (dest, directions)
    
     def getOrders(self):
         return self.orderList
     
     def getAntNum(self):
         return self.num
     
     def hasOrders(self):
         #if len(self.orderList) > 0:
         if self.orderList != (None,None):
            return True
         else:
            #self.targDest = None # No longer has a target Dest
            return False
    
     def noOrders(self):
        return True
     
     def executeOrders(self, ants, bot, antObj):
         
         if not self.hasOrders():
            return False
        
         #self.objEnemyList = [] # clear old seen enemy Ants
         enemyList = []
        # logging.warning(self.num)
        # logging.warning(self.getLocation())
         #logging.debug("attack Radius: %s"% (str(bot.attackRadius)))
        
    
         
        # logging.debug("direction: %s"% (str(direction)))
         
         #if (len(direction)) == 0:
          #   direction = ants.direction(self.loc, self.orderList[0]) # ant_loc, food_loc # Compute fastest route to food with no direction given only a location
 
         #logging.warning(self.loc)
         
         dest = self.orderList[0]
         self.targDest = dest
         """
         if self.targDest in bot.hills:
             for friendlyAnt in self.friendlyList:
                 friendly_loc = friendlyAnt.getLocation()
                 dirList = ants.direction(friendlyAnt.getLocation(), self.targDest) # Find fastest way to get to hill
                 if len(dirList) != 0:
                    for direction in dirList:
                        new_loc = ants.destination(friendly_loc, direction)
                        if ants.unoccupied(new_loc) and ants.passable(new_loc) and new_loc not in self.targeted:
                                friendlyAnt.giveOrders(None, None) # Only 1 move
                                friendlyAnt.setMoved(True)
                                friendlyAnt.setLocation(new_loc)
                                self.targeted.append(new_loc)
                                ants.issue_order((friendly_loc, direction)) # Move first Ant 
                                break
          """   
         shuffle(self.directionList)
         
         
         direction = self.directionList[0]
         ant_loc = antObj.getLocation()  
         
         
         #row, col = ant_loc # current ant location
         
        # if ant_loc in bot.hills: 
           #  bot.hills.remove(ant_loc) # Remove enemy hill from list as ant is in enemy hill
  
         
         if len (self.orderList[1]) < 1: # No Directions in direction list
            self.orderList = (None, None)
            #bot.removeTargets(dest)

            logging.debug("Got orders without directions")
            return False
        # orderlist: (dest, ['n', 'w'])
        
         
         #if ant_loc not in bot.targeted:
            # bot.targeted.append(ant_loc)
             
         new_loc = ants.destination(self.loc, self.orderList[1][0]) # get next location from current state
        
         #logging.debug("objEnemyList: %s"% (str(self.objEnemyList)))
         
         """
         #for enemy in self.objEnemyList:
         for enemy in bot.enemyList:
             #if self.targDest in bot.hills: # Go after Hills
               #  break
             enemyDist = ants.distance(new_loc, enemy[0])
             
             if enemyDist <= bot.attackRadius: # new location is within attacking distance of ant
                 enemyList.append(enemy[0]) # List of all enemies that will attack ant at new location
          
              # SHOULD ADD TO LIST IF CLOSE TO HILL AND HANDLE DEFENSE
         
             #if len(ants.my_hills()) < 1:
               # break
             if ants.distance(antObj.getLocation(), bot.myHill[0]) < (bot.attackRadius + 8) and ants.distance(bot.myHill[0], enemy[0]) < (bot.attackRadius + 8):
                direct = ants.direction(enemy[0], antObj.getLocation())
                choice = len(direct)
                newLoc = ants.destination(antObj.getLocation(), direct[choice-1])
                if ants.passable(newLoc):
                    bot.removeTargets(dest) # Remove old destination as a target
                    self.orderList = (None, None)
                    #bot.targeted.append(newLoc)
                    #bot.targets[newLoc] = antObj # You are now targeting newLoc
                    self.targDest = newLoc # Set new target location to newLoc
                    return direct[choice-1]
                else:
                    return False # REMOVE
                    continue
           
            """    
             
         
        # row, col = antObj.getLocation() # current ant location
         """
         closeAntObj = None
         
         if (row, col-1) in bot.antDict.keys():
             closeAntObj = bot.antDict[(row, col-1)]
         elif (row, col+1) in bot.antDict.keys():
             closeAntObj = bot.antDict[(row, col+1)]
         elif (row - 1, col) in bot.antDict.keys():
             closeAntObj = bot.antDict[(row - 1, col)]
         elif (row + 1, col) in bot.antDict.keys():
             closeAntObj = bot.antDict[(row + 1, col)]
         elif ([row - 1], col - 1) in bot.antDict.keys():
             closeAntObj = bot.antDict[(row - 1, col - 1)]
         elif (row + 1, col - 1 ) in bot.antDict.keys():
             closeAntObj = bot.antDict[(row + 1, col - 1)]
         elif (row, col + 2) in bot.antDict.keys():
             closeAntObj = bot.antDict[(row, col + 2)]
         elif (row, col - 2) in bot.antDict.keys():
             closeAntObj = bot.antDict[(row, col - 2)]
         """
            
         # logging.debug("Ant Dict: %s"% (str(bot.antDict)))
         """ 
         if closeAntObj != None and closeAntObj in bot.antList and closeAntObj.moved == False and len(enemyList) > 0 and direction != None: # len(enemyList) > 0
                 logging.debug("closeAntObj: %s"% (str(closeAntObj)))
                 direct = ants.direction(closeAntObj.getLocation(), enemyList[0]) # Return 1 or two best ways to move towards enemy
                 if len(direct) != 0:
                     #direct.append("n")
                     for directs in direct:
                         close_new_loc = ants.destination(closeAntObj.getLocation(), directs)
                         if ants.distance(close_new_loc, enemyList[0]) <= bot.attackRadius and close_new_loc not in bot.targeted and ants.passable(close_new_loc) and ants.unoccupied(close_new_loc): # new location is within attack radius
                             #del bot.antDict[closeAntObj.getLocation()] # Remove key from dictionary
                             bot.antDict[close_new_loc] = closeAntObj
                             bot.targets[close_new_loc] = closeAntObj
                             closeAntObj.setMoved(True)
                             bot.targeted.append(close_new_loc)
                             closeAntObj.giveOrders = ((None, None))
                             ants.issue_order((closeAntObj.getLocation(), directs))
                             closeAntObj.setLocation(close_new_loc)
                            #if direction != None:
                               # closeAntObj.giveOrders((close_new_loc, directs))
                        
                             return direction
                             #closeAntObj.targDest = close_new_loc
                             #bot.targeted.append(close_new_loc)
                             #closeAntObj.setLocation(close_new_loc)
                             #logging.debug("USED ATTACK CODE: %s"% (str(closeAntObj.getLocation())))
                             #ants.issue_order((closeAntObj.getLocation(), directs))
                            # directz = ants.direction(antObj.getLocation(), enemyList[0])
                             #move_to = ants.destination(ant_loc, directz[0])
                             #if len (directz) > 0 and ants.passable(move_to) and move_to not in bot.targeted:
                              #  self.targDest = move_to
                               # bot.targeted.append(move_to)
                                #return directz[0]
                      
         
         
         
         bestDir = None # The direction to avoid conflict
         for direction in self.directionList:
             newLoc = ants.destination(antObj.getLocation(), direction)
             if newLoc in bot.targeted or not ants.passable(newLoc):
                 continue
             for enemyAnt in enemyList: # For every ant in list that new location will violate
                if ants.distance(newLoc, enemyAnt) > bot.attackRadius and ants.passable(newLoc) and ants.unoccupied(newLoc) and newLoc not in bot.targeted:
                    bestDir = direction   # Move direction away from enemy
                else:
                    bestDir = None
                    
         if bestDir != None:
                #bot.removeTargets(dest) # Remove old destination as a target
                self.orderList = (None, None) # Clear orders list
                #del bot.antDict[antObj.getLocation()] # Remove old location from dict
                #bot.targets[newLoc] = antObj # You are now targeting newLoc
                self.targDest = newLoc # Set new target location to newLoc
                self.moved = True
                return bestDir 
         """   
        # if len(enemyList) > 1:
         #   return False # Cannot find a safe direction stay still
                       
            
        # logging.debug("new loc: %s"% (str(new_loc)))
        
               
        # if not ants.unoccupied(new_loc) or new_loc in bot.targeted:
           #  return False
         
         if not ants.passable(new_loc) or not ants.unoccupied(new_loc) or new_loc in bot.targeted:   
             if dest in bot.targets.keys():          
                 bot.removeTargets(dest)
             self.orderList = (None, None)
             #return direction # CHECK IF BETTER TO STAY PUT
             return False
         
         #if new_loc in bot.targeted:
          #   return False
             
             """
             shuffle(self.directionList)
             for direction in self.directionList: #('s','e','w','n'):
                    dist = ants.distance(antObj.getLocation(), bot.myHill[0])
                    new_loc = ants.destination(antObj.getLocation(), direction) # get new location given current loc and direction to move
                    distNew = ants.distance(new_loc, bot.myHill[0])
                    if dist > distNew and distNew < 5: # Moving closer to hill, don't want this
                        continue
                    if ants.unoccupied(new_loc) and ants.passable(new_loc) and new_loc not in bot.targeted and new_loc not in bot.targets.keys(): #and new_loc not in self.targeted:
                        bot.targets[new_loc] = antObj # Set new target location to newLoc
                        self.targDest = new_loc
                        bot.ordersToDelete.append(antObj)
                        return direction
             return False
             """
         
         
         if ants.unoccupied(new_loc) and new_loc not in bot.targeted: #and new_loc not in bot.targeted: # No ant at new location:
             if len(self.orderList[1]) > 0:
                 direct = self.orderList[1].pop(0) # Remove front direction in list
                 #bot.targeted.append(new_loc) # targeted contains list of ants that ae going to be targeted next
                 #bot.targeted.remove(ant_loc) # No longer in old location
                 self.targDest = dest
                 self.moved = True
             if len(self.orderList[1]) == 0: # There is no directions left
                 if dest in bot.targets.keys():
                     bot.removeTargets(dest)
                     self.giveOrders((None, None))
                 
             self.moved = True 
             return direct

          #  logging.debug("Has Orders: %s"% (str(self.hasOrders())))
         #self.moved = False
         #return False
         #del bot.antDict[antObj.getLocation()]
         #bot.removeTargets[dest]
         self.setMoved(True)
         if direction == None:
             return False
         else:   
             return direction
         
     
            