#include <stdio.h>
#include <math.h>
#include <stdlib.h>

#define CONV 3.14159/180


int test(int *x){
    *x = 150;
    return 0;
}

int main()
{

    int x = 5;
    if (x == 5)
    {
        x = 6;
    }
    printf("%d\n",x);
    int y;
    test(&y);
    printf("%d\n",y);
    
    return 0;
}
