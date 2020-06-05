#include <netdb.h>
#include <strings.h>
#include <cstring>
#include <stdlib.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <thread>
#include <vector>
#include <mutex>
#include <math.h>
#include <iostream>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
using namespace std;


int skt;
struct sockaddr_in server;
char const *ip;
int puerto;
int armonicos;


/*
    Creamos un socket genércio
*/
void creaSocket()
{
    skt = socket(AF_INET, SOCK_DGRAM, 0);
    memset(&server, 0, sizeof(server));
    server.sin_family = AF_INET;
    server.sin_port = htons(puerto);
    server.sin_addr.s_addr = inet_addr(ip);
}

//Realiza la suma de la funcion periodica diente sierra
double realizaSuma(double x, int k)
{
    if (k == 0)
        return 0.0;
    double total = 0.0, acum;
    for (int i = 1; i < k; i++)
    {
        //Serie de fouerier diente de sierra
        acum = ((pow(-1.0,(double)(i+1)))/(i*M_PI))*sin(i*M_PI*x);
        total += acum;
    }
    return total;
}

/*
    Envia las coordendas al sservidor del termino n
*/
void enviaCoordenadas(int terminos)
{
    double dimensiones = M_PI / 400;
    double x, y, x_temp, y_temp;
    int x_1, y_1, x_2, y_2;
    int coor[4];
    creaSocket();;
    for (int n = terminos - 1; n < terminos; n++)
    {
        x_1 = 1;
        x_2 = 1;
        for (int i = 0; i <= 800; i++)
        {
            x = ((-M_PI) + (dimensiones * i));
            if (x > M_PI)
                x = M_PI;
            if (x > -0.001 && x < 0.001)
                x = 0.0;
            y = 2*realizaSuma(x,n);
            if (y > -0.001 && y < 0.001)
                y = 0.0;
            x_1++;
            y_1 = 150 - (((300 * (y + 1.4)) / 2.8) - 145);
            x_temp = ((-M_PI) + (dimensiones * (i + 1)));
            if (x_temp > M_PI)
                x_temp = M_PI;
            if (x_temp > -0.001 && x_temp < 0.001)
                x_temp = 0.0;
 
            y_temp = 2*realizaSuma(x_temp,n);
            if (y_temp > -0.001 && y_temp < 0.001)
                y_temp = 0.0;
            x_2++;
            y_2 = 150 - (((300 * (y_temp + 1.4)) / 2.8) - 145);
            coor[0]=x_1;
            coor[1]=y_1;
            coor[2]=x_2;
            coor[3]=y_2;
            usleep(2300);
            //Mandamos las coordenadas al servidor para el término n
            sendto(skt, &coor, 4*sizeof(double), MSG_CONFIRM, (const struct sockaddr *)&server, sizeof(server));
        }
    }
    close(skt);
}

/*
    La funcion rellan un arreglo de entero con ceros y una bandera f
*/
void flag(int f)
{
    int coor[4];
    creaSocket();;
    coor[0]=0;
    coor[1]=0;
    coor[2]=0;
    coor[3]=f;
    usleep(60);
    sendto(skt, &coor, 4*sizeof(double), MSG_CONFIRM, (const struct sockaddr *)&server, sizeof(server));

}
/*
    Esta función crea dos hilos 
    1 para mandar las coordendas de los armonicos 'arm'
    2 Para mandar un aviso al servior que se han mandado todas las croordeandas del armonico arm

*/
void paint_erase_grafic(int arm, int f){
    thread th;
    th = thread(enviaCoordenadas, arm);
    th.join();
    th = thread(flag, f);
    th.join();
}


int main(int argc, char const *argv[])
{

    if (argc != 2)
    {
        cout << "MODO DE USO >: ./cliente <ip>" << endl;
        return -1;
    }

    ip = argv[1];    
    puerto = 9005;
    int n = 3;
    do{
        paint_erase_grafic(n, -5);
        paint_erase_grafic(n, -6);
        n++;
    }while(true);
    
    return 0;
}
