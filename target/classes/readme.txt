Readme.txt:  Homework 6
By Alex Teich          for COMP 504
Fall 2018


I. View Menus
I chose to have three menus: one for the update strategies, one for the interact strategies, and one to specify what type of switching action should be done (an interact switch, an update switch, or both). If the user selects only one choice, only that type of strategy will be switched based on the selections from the respective menu. If both, or neither, of the choices are selected, both types of strategies are switched based on the menu selections. For example, if I have nothing selected in the interact menu, I don’t want the situation in which I don’t know if that’s going to affect my interact strategy or not; also, I may want to be able to clear my interact strategy to make it null, and this system allows me to do that.


II. Interaction Strategies
I made seven new strategies that affect how the balls interact. Unfortunately, two of them stopped working just before the deadline: the smasher and blob strategies. These were both a source of pride and were working perfectly earlier in the day. I don’t know what I might have altered just before submission time that interferes with them. Quite a heartbreaker for me!


BILLIARD BALL:  Instead of colliding with a ball in the neutral way that merely switches the signs of the ball’s velocities according to their relative positions, the billiard ball simulates an elastic collision by exerting force along the vector of the two centers, both on the destination ball and backward onto the source ball. If two billiard balls collide, they both project their velocity vectors onto this center-to-center vector and force in both directions. If a billiard ball collides with a non-billiard ball, the effect on the billiard ball is the same whether the non-billiard is moving or not (the billiard applies force to itself from the collision, but receives none from the other ball).


SMASHER: Destroys the destination ball. 


SPLITTER: Splits the destination ball into two new balls that are copies of the destination ball and considered new independent observers.


THE BLOB: Eats the destination ball, which is then drawn inside of it and loses its independence and influence in the ball world. 
When the blob hits a smasher, the blob and its contents are destroyed. When it hits a splitter, it keeps all of its eaten bits, but a new empty blob is created to roam independently. When it hits another blob, the two blobs collide neutrally and leave each other alone 
(Since blobs don’t consume each other, they can compete to eat the most balls, like a race).


SWAPPER: Trades its update strategy with the destination ball. 


NULLIFIER: Kills the destination ball by setting its update strategy to null. When it hits a blob, it is eaten with no effect on the blob.


LEADER: Makes the destination ball into a follower, which then hounds it mercilessly like a rabid fan. 


III. Update Command
The first observer that executes the update command is added to an array of observers that have “visited” the command. Each observer that follows can interact with the balls that have already been there, and in this way, all ball interactions have a chance to happen; also, all observer information is, in this way, imported into the update command. Once all observers have visited, we can then invoke their update strategies.


IV. Wrapper System
Because strategies affect balls in many ways, and we are not allowed to add fields or methods to the ball class, I put various fields and methods into wrapper strategies. For example, when a blob eats another ball, the eaten ball has its update strategy put into a wrapper that keeps track of who the host blob is, and what the position of the eaten ball should be relative to the host blob.


V. Factories and Helpers
I needed to put certain methods in helper classes; for example, an unwrapped method that can extract a base strategy from a strategy that carries wrappers.