#include "eyebot.h"
#include <stdio.h>
#include <math.h>
#include <stdlib.h>

#define CUBES 4
#define POINTS 180

#define CUBE_R_MIN 70
#define CUBE_R_MAX 85
#define CUBE_G_MIN 60
#define CUBE_G_MAX 75
#define CUBE_B_MIN 15
#define CUBE_B_MAX 30

#define X_TARGET 150
#define Y_TARGET 1800

#define CONV 0.01745328

#define ARRIVED 0
#define CUBE_DETECTED 1
#define CUBE_LOST 2
#define DRIVE_STALLED 3
#define OKAY 4
#define TASK_COMPLETE 5
#define ERROR 10;


int status;


int isStalled()
{
  int x1, x2, y1, y2, p1, p2;
  VWGetPosition(&x1, &y1, &p1);
  VWGetPosition(&x2, &y2, &p2);

  if ((abs(x2 - x1) < 5) && (abs(y2 - y1) < 5)) return DRIVE_STALLED;

  return OKAY;
}

int push()
{
  int x, y, p;
  VWGetPosition(&x, &y, &p);

  double diff = (X_TARGET - x) / (Y_TARGET - y);

  int theta = (int) atan(diff) * CONV;

  VWTurn(theta, 50);

  int dist = sqrt(pow(X_TARGET-x, 2) + pow(Y_TARGET-y, 2));

  VWStraight(dist, 50);

  return ARRIVED;
}

// float adjDistance(int hypo, int angle){
//   LCDPrintf("adjacent anGGle: %d\n",cos(angle/CONV));
//   LCDPrintf("Hypot: %d\n",hypo);
//   float adj = cos(angle/CONV) * hypo;
//   return adj;
// }

void refreshImage(BYTE *im)
{
  CAMGet(im);
  LCDImage(im);
}



int detectCubes(BYTE *im, int *x)
{
  for (int i = 0; i < QQVGA_SIZE - 3; i+=3) {
    if ((im[i] <= CUBE_R_MAX && im[i] >= CUBE_R_MIN) &&
        (im[i+1] <= CUBE_G_MAX && im[i+1] >= CUBE_G_MIN) &&
        (im[i+2] <= CUBE_B_MAX && im[i+2] >= CUBE_B_MIN) && (i == 29040)) {

          *x = i;

          return CUBE_DETECTED;
    }
  }

  return CUBE_LOST;
}

int getQuadrant()
{
  int x, y, p, xdiff, ydiff;

  VWGetPosition(&x, &y, &p);

  xdiff = X_TARGET - x;
  ydiff = Y_TARGET - y;

  if (xdiff > 0 && ydiff > 0) return 1;
  if (xdiff < 0 && ydiff > 0) return 2;
  if (xdiff < 0 && ydiff < 0) return 3;
  if (xdiff > 0 && ydiff < 0) return 4;

  else return ERROR;
}

//search for the cubes which are most densely congregated
int initialScan(int *initial_angle, int *initial_dist, int *cube_x_post, int *cube_y_post)
{
  // int initial_x, initial_y, initial_angle;
  // VWGetPosition(&initial_x,&initial_y,&initial_angle);
  int x;
  BYTE  img[QQVGA_SIZE];
  CAMInit(QQVGA);
  refreshImage(img);
  CAMGet(img);
  LCDImage(img);
  
  while(detectCubes(img, &x) != CUBE_DETECTED){
    VWTurn(30,10);
    CAMGet(img);
    LCDImage(img);
    

  }
  OSWait(50);
  while(detectCubes(img, &x) == CUBE_DETECTED){
      
      refreshImage(img);
      int hypo;
      hypo = PSDGet(PSD_FRONT);
      int X, Y, angle;
      VWGetPosition(&X,&Y,&angle);
      *initial_angle = angle;
      
      VWTurn(-angle,30);
      LCDPrintf("found cube: %d\n",angle);
      // VWSetPosition(initial_x,initial_y,initial_angle);
      double opposite = sin(angle * CONV) * hypo;
      *initial_dist = opposite;
      double adj = sqrt(pow(hypo,2)-pow(opposite,2));
      *cube_x_post = X + adj;
      *cube_y_post = Y + opposite;
       

      VWWait();
      



  }

  return 0;




}

int findCubeDensity(int *initial_angle, int *initial_dist, int *cube_x_post, int *cube_y_post)
{
  FILE *f;
  f = fopen("test.txt", "a");
  int x;
  BYTE  img[QQVGA_SIZE];
  CAMInit(QQVGA);
  refreshImage(img);
  CAMGet(img);
  LCDImage(img);
  while(detectCubes(img, &x) != CUBE_DETECTED){

    LCDPrintf("cube lost\n");
    fprintf(f, "cube lost");
    VWTurn(30,10);
    CAMGet(img);
    LCDImage(img);
    OSWait(50);


    while(detectCubes(img, &x) == CUBE_DETECTED)
    {
      
      LCDPrintf("cube detected\n");
      fprintf(f, "cube found\n");
      refreshImage(img);
    }
    
    
    
    

  }
  
  return 0;

}

int driveToCenter(int x, int y){
  int current_x, current_y, current_angle;
  VWGetPosition(&current_x,&current_y,&current_angle);
  LCDPrintf("current x: %d\n",current_x);
  LCDPrintf("current y: %d\n",current_y);
  int adj = abs(y-current_y);
  int opp = abs(x-current_x);
  

  
    VWStraight(-40,10);
    VWWait();
    
    VWTurn(-current_angle-80, 30);
    
    
    
    //VWTurn(180,30);
    VWWait();
    LCDPrintf("adj: %d\n", adj);
    VWStraight(adj,30);
    VWWait();
    VWTurn(-90,20);
    VWWait();
    VWStraight(opp,30);
    VWWait();
    
    
  
  VWGetPosition(&current_x,&current_y,&current_angle);

    if (current_angle != 0)
    {
      if (current_angle < 0)
      {
        LCDPrintf("current_angle: %d\n",current_angle);
        VWTurn(current_angle,15);
        VWWait();
      }
      if(current_angle > 0)
      {
        LCDPrintf("current_angle: %d\n",current_angle);
        VWTurn(-current_angle,15);
        VWWait();
      }
      
    }
  return 0;
}

//From the center of the map, go behind the targeted cube
int goBehindCube(int adj, int adj_dist,int buffer, int opp_dist, int turning_angle){
    VWTurn(adj,20);
    VWWait();
    VWStraight(adj_dist + buffer, 30);
    VWWait();
    VWTurn(turning_angle,20);
    VWWait();
    VWStraight(opp_dist,30);
    VWWait();
    VWTurn(turning_angle,20);
    VWWait();
return 0;
}

int goToside(bool side){
    //right side
    if(side){
    VWStraight(-30,15);
    VWWait();
    VWTurn(-90,20);
    VWWait();
    VWStraight(200,20);
    VWWait();
    VWTurn(90,20);
    VWWait();
    VWStraight(140,20);
    VWWait();
    VWTurn(90,20);
    VWWait();
    }
    //left side
    else{
      VWStraight(-30,15);
    VWWait();
    VWTurn(90,20);
    VWWait();
    VWStraight(200,20);
    VWWait();
    VWTurn(-90,20);
    VWWait();
    VWStraight(140,20);
    VWWait();
    VWTurn(-90,20);
    VWWait();
    }
 
 
  return 0;
}


int goToCubeAndPush(int angle, int hypo , int target_x, int target_y){
  
  //int opp_buffer = 120;
  
  //if cube is in the fourth quadrant
  if (angle < 0 && angle < -90)
  {
    int adj_buffer = 190;
    int opp_buffer = 150;
    int adj_angle = -180 - angle;
    double adj_dist = cos(adj_angle * CONV) * hypo;
    double opp_dist = sqrt(pow(hypo,2)-pow(adj_dist,2));

    //Drive behind the cube
    goBehindCube(adj_angle,adj_dist,adj_buffer,opp_dist + opp_buffer,90);

    //Now drive to the cluster
    int current_x, current_y, current_angle;
    VWGetPosition(&current_x,&current_y,&current_angle);
    int adj_dist_topush = target_x - current_x;
    int opp_dist_topush = target_y - current_y;
    LCDPrintf("current_x: %d cube x: %d",current_x,target_x);
    
    VWStraight(adj_dist_topush,50);
    VWWait();

    //Get to the right side of the cube
    goToside(True);
    VWStraight(opp_dist_topush,50);
    VWWait();
  }
  
  //if angle is in the first quadrant
  else if (angle > 0 && angle > 90)
  {
    int adj_buffer = 250;
    //int opp_buffer = 150;
    int adj_angle = 180 - angle;
    double adj_dist = cos(adj_angle * CONV) * hypo;
    double opp_dist = sqrt(pow(hypo,2)-pow(adj_dist,2));

    //Drive behind the cube
    goBehindCube(adj_angle,adj_dist,adj_buffer,opp_dist,-85);

    //Now Drive to the cluster
    int current_x, current_y, current_angle;
    VWGetPosition(&current_x,&current_y,&current_angle);
    VWStraight(target_x - current_x, 50);
    
    //if difference in y coordinates is greater than 300
    if (abs(target_y - current_y) > 200)
    {
      if (target_y > current_y )
      {
        goToside(True);
        VWStraight(target_y-current_y,20);
        VWWait();
      }
      
      
    }
    VWWait();
    



  }
  else if (angle < 0 && angle > -90)
  {

    int buffer_adj = 270;
    int buffer_opp = 70;

    //This is the angle that is used to calculate the distances
    int adj_angle = -angle-90;
    
    //This is the angle that is used to turn once the robot has detected a cube
    int angle_to_turn = -angle-80;
    VWTurn(angle_to_turn,50);

    //Calculate the adjacent distance and add the buffer for the distance to drive
    double adj = cos(adj_angle * CONV) * hypo;       
    double dist_to_drive = adj + buffer_adj;
    LCDPrintf("Dist to drive: %f\n", dist_to_drive);

    //Calculate the opposite distance and add the buffer for the distance to drive
    double opp = sqrt(pow(hypo,2)-pow(adj,2)) + buffer_opp;
    LCDPrintf("opposite dist: %f\n",opp);
    
    int X_position, Y_position, angle;
    VWGetPosition(&X_position, &Y_position, &angle);

    

      //Go behind cube
      VWWait();
      
      VWWait();                                                                              
      VWStraight(dist_to_drive, 50);
      //LCDPrintf("Now pushing: %d\n",pushing_dist);
      VWWait();
      VWTurn(90,50);
      VWWait();
      VWStraight(opp,50);
      VWWait();
      VWTurn(90,50);
      VWWait();

      //push cube
      VWGetPosition(&X_position, &Y_position, &angle);
      int pushing_dist = target_y - Y_position;
      VWStraight(pushing_dist,50);
      VWWait();
      if ((target_x - X_position) > 200)
      {
        LCDPrintf("target_x - X_postion: %d\n", target_x - X_position);
        goToside(False);
        VWStraight(target_x - X_position,50);
        VWWait();
        
      }
      
      LCDPrintf("X_position: %d\n", X_position);
      LCDPrintf("Y_position: %d\n", Y_position);
      //driveToCenter(X_position,Y_position);
      

  }
  
  

  return 0;
}

// int main()
// {
//    int initial_angle, initial_opp_dist, initial_cube_x, initial_cube_y;
//    findCubeDensity(&initial_angle, &initial_opp_dist, &initial_cube_x, &initial_cube_y);
//   /* code */
//   return 0;
// }



// /*-------- DO NOT DELETE THIS...Duh--------------*/
int main()
{ /*Read This first: use initialScan to find the most dense cluster first, and then always set the cluster to be at the top left hand coner of the robot
   using VWSetPosition*/
  int initial_angle, initial_opp_dist, initial_cube_x, initial_cube_y;
  initialScan(&initial_angle, &initial_opp_dist, &initial_cube_x, &initial_cube_y);
  // VWSetPosition(1000,1000,0);
  int x;
  BYTE  img[QQVGA_SIZE];
  CAMInit(QQVGA);
  refreshImage(img);

//  while(1) {
    CAMGet(img);
    LCDImage(img);
    // int X_position2, Y_position2, angle2;
    // VWGetPosition(&X_position2, &Y_position2, &angle2);
    // LCDPrintf("Current angle: %d",angle2);

    while(detectCubes(img, &x) != CUBE_DETECTED) {
      VWTurn(-25, 15);
      CAMGet(img);
      LCDImage(img);

    

    OSWait(50);


    if ((detectCubes(img, &x) == CUBE_DETECTED)) {
   
      refreshImage(img);
      int hypo;
      hypo = PSDGet(PSD_FRONT);

      

      LCDPrintf("Distance to push: %d\n",initial_opp_dist);
      LCDPrintf("Cube X: %d\n", initial_cube_x);
      LCDPrintf("Cube Y: %d\n", initial_cube_y);
      LCDPrintf("hypothenuse: %d\n",hypo);
      int X_position, Y_position, angle;
      VWGetPosition(&X_position, &Y_position, &angle);

      goToCubeAndPush(angle,hypo,initial_cube_x,initial_cube_y);
      driveToCenter(X_position,Y_position);
      refreshImage(img);
      // if cube is is second quadrant
      //This part is suppose to go under goToCubeAndPush
    }
    
    }

  return 0;
}


