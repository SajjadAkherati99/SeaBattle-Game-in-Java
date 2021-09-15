// Include your C header files here

#include "pth_msort.h"
#include <stdlib.h>
#include <stdio.h>

struct array_struct {
    int* array;
    int L;
    int R;
};

struct serial_merge_struct {
    int* arr1;
    int* arr2;
    int* merged;
    int size;
};

struct parallel_merge_struct {
   int block_ID;
   int* arr1;
   int* arr2;
   int* merged;
   long long int size;
};

void* QuickSort(void* void_array_struct) {
	
	struct array_struct* arr_atruct = (struct array_struct*) void_array_struct;
	
	int* arr = arr_atruct -> array;
	int L = arr_atruct -> L ;
	int R = arr_atruct -> R;
	
    int P = 0;
    if (L < R){
		
        P = partition(arr, L, R);
		
		struct array_struct arr_part1 = {arr, L, (P - 1)};
		struct array_struct arr_part2 = {arr, (P + 1), R};
		
        QuickSort((void*) (&arr_part1));
        QuickSort((void*) (&arr_part2));
    }
	
	return NULL;
}

int partition (int* arr, int L, int R) {

    int pivot = arr[R];
    int temp =0;
    int i = (L - 1);
	
	int j;
	
    for (j = L; j < R; j++){
        if (arr[j] < pivot){
            i++;
            temp = arr[i];
            arr[i] = arr[j];
            arr[j]=temp;
        }
    }
    temp = arr[i+1];
    arr[i+1] = arr[R];
    arr[R] = temp;
	
    return (i + 1) ;
}



void* mergeSort(void* void_array_struct) {
	struct array_struct* arr_atruct = (struct array_struct*) void_array_struct;
	
	int* arr = arr_atruct -> array;
	int L = arr_atruct -> L ;
	int R = arr_atruct -> R;
	
    if (L < R) {
		
        int M = L + (R - L) / 2;
		
		struct array_struct arr_part1  = {arr, L, M};
		struct array_struct arr_part2 = {arr, (M + 1), R};
		
        mergeSort((void*) (&arr_part1));
        mergeSort((void*) (&arr_part2));
 
        int i, j, k;
		int n1 = M - L + 1;
		int n2 = R - M;
		
		int* L_arr = (int*) malloc ( sizeof(int) * n1 );
		int* R_arr = (int*) malloc ( sizeof(int) * n2 );
	 
	 
		for (i = 0; i < n1; i++)
			L_arr[i] = arr[L + i];
		for (j = 0; j < n2; j++)
			R_arr[j] = arr[M + 1 + j];
	 
		
		i = 0;
		j = 0; 
		k = L;
		while (i < n1 && j < n2) {
			if (L_arr[i] <= R_arr[j]) {
				arr[k] = L_arr[i];
				i++;
			}
			else {
				arr[k] = R_arr[j];
				j++;
			}
			k++;
		}
		
		while (i < n1) {
			arr[k] = L_arr[i];
			i++;
			k++;
		}
		
		while (j < n2) {
			arr[k] = R_arr[j];
			j++;
			k++;
		}
    }
	
	return NULL;
}

void merge(int* arr, int L, int M, int R) {
    
	int i, j, k;
    int n1 = M - L + 1;
    int n2 = R - M;
	
	int* L_arr = (int*) malloc ( sizeof(int) * n1 );
	int* R_arr = (int*) malloc ( sizeof(int) * n2 );
 
 
    for (i = 0; i < n1; i++)
        L_arr[i] = arr[L + i];
    for (j = 0; j < n2; j++)
        R_arr[j] = arr[M + 1 + j];
 
    
    i = 0;
    j = 0; 
    k = L;
    while (i < n1 && j < n2) {
        if (L_arr[i] <= R_arr[j]) {
            arr[k] = L_arr[i];
            i++;
        }
        else {
            arr[k] = R_arr[j];
            j++;
        }
        k++;
    }
	
    while (i < n1) {
        arr[k] = L_arr[i];
        i++;
        k++;
    }
	
    while (j < n2) {
        arr[k] = R_arr[j];
        j++;
        k++;
    }
}


void* serial_merge(void* serial_merge_void) {
	
	struct serial_merge_struct* SMSA = (struct serial_merge_struct*) serial_merge_void;
	int arrOutSize = 2*( SMSA -> size);
	int* arr1 =  SMSA -> arr1;
	int* arr2 =  SMSA -> arr2;
	int* merged =  SMSA -> merged;
	
	int pArr1 = 0;
	int pArr2 = 0;
	
	int flag1= 1;
	int flag2= 1;
	
	int i;
	
	for (i=0 ; i<arrOutSize ; i++) {
		if (((arr1[pArr1] < arr2[pArr2]) || (flag2==0)) && (flag1==1)) {
			merged[i] = arr1[pArr1];
			pArr1++;
			flag1 = pArr1 < SMSA -> size;
		}
		else {
			merged[i] = arr2[pArr2];
			pArr2++;
			flag2 = pArr2 < SMSA -> size;
		}
	}
	
	return NULL;
}

long long int binarySearch1(int arr[], long long int L, long long int R, int x) {
	
	long long int mid; 
	
    if (R > L) {
		mid = L +(long long int)( (R - L)/2);
		if (arr[mid] < x) {
			return binarySearch1(arr, mid+1, R, x);
		}
		
      	return binarySearch1(arr, L, mid, x);   
    }
    else if (x > arr[R]) {
		return (R+1);
	}
	
    return R;
}

long long int binarySearch2(int arr[], long long int L, long long int R, int x) {
	
    long long int mid;
	
    if (R > L) {
		mid = L +(long long int)( (R - L)/2) ;
		if (x < arr[mid] ) {
			return binarySearch2(arr, L, mid, x);
		}
		
        return binarySearch2(arr, mid+1, R, x);
    }
    else if (x >= arr[R]){
		return (R+1);
	}
	
    return R;
}

void* parallel_merge (void* parallel_merge_void) {
	
	struct parallel_merge_struct* PMSA = (struct parallel_merge_struct*) parallel_merge_void;

	long long int size_in = PMSA -> size;
	long long int N = 8 * size_in;
	
	int* arr1 = PMSA -> arr1;
	int* arr2 = PMSA -> arr2;
	int* merged = PMSA -> merged;

	int block_ID = PMSA -> block_ID;
	long long int L = (block_ID - 1)*(N/8);
	int R = (block_ID)*(N/8) - 1;

	long long int L1 = 0;
	long long int R1 = N/2 -1;
	long long int L2 = 0;
	long long int R2 = N/2 -1;

	long long int i;
	long long int j = 0;
	long long int k = 0;
	long long m;
	
	for(i = L ; i <= R ;i++){
		j = binarySearch1(arr2, j, (N/2 - 1), arr1[i]);
		merged[j + i] = arr1[i];
		
		k = binarySearch2(arr1, k, (N/2 - 1), arr2[i]);
		merged[k + i] = arr2[i];
	}
	
	return NULL;
}


void merge_sort_parallel(const int* values, unsigned int N, int* sorted) {
	
	int* value_arr = (int*)(values);
	int* temp_arr = (int*) malloc ( sizeof(int) * N );
	
	struct array_struct arr_part1 = {value_arr, 0, N/4 -1 };
	struct array_struct arr_part2 = {value_arr, N/4, N/2 -1 };
	struct array_struct arr_part3 = {value_arr, N/2, 3*N/4 -1 };
	struct array_struct arr_part4 = {value_arr, 3*N/4, N -1 };
	

	pthread_t handle1, handle2, handle3, handle4;
	
	pthread_create( &handle1, NULL, QuickSort, (void*)(&arr_part1) );
	pthread_create( &handle2, NULL, QuickSort, (void*)(&arr_part2) );
	pthread_create( &handle3, NULL, QuickSort, (void*)(&arr_part3) );
	pthread_create( &handle4, NULL, QuickSort, (void*)(&arr_part4) );
	
	pthread_join(handle1, NULL); 
	pthread_join(handle2, NULL);
	pthread_join(handle3, NULL);
	pthread_join(handle4, NULL);
	
	struct serial_merge_struct arr_half1 = {&value_arr[0], &value_arr[N/4], &temp_arr[0], N/4 };
	struct serial_merge_struct arr_half2 = {&value_arr[N/2], &value_arr[3*N/4], &temp_arr[N/2], N/4};
	
	pthread_t handle5, handle6;
	
	pthread_create( &handle5, NULL, serial_merge, (void*) (&arr_half1) );
	pthread_create( &handle6, NULL, serial_merge, (void*) (&arr_half2) );
	
	pthread_join(handle5, NULL); 
	pthread_join(handle6, NULL);
	
	struct parallel_merge_struct block1 = {1, temp_arr, &temp_arr[N/2], sorted ,(long long int)(N/8)};
	struct parallel_merge_struct block2 = {2, temp_arr, &temp_arr[N/2], sorted ,(long long int)(N/8)};
	struct parallel_merge_struct block3 = {3, temp_arr, &temp_arr[N/2], sorted ,(long long int)(N/8)};
	struct parallel_merge_struct block4 = {4, temp_arr, &temp_arr[N/2], sorted ,(long long int)(N/8)};
	
	pthread_t handle7, handle8, handle9, handle10;
	
	pthread_create( &handle7,  NULL, parallel_merge, (void*) (&block1) );
	pthread_create( &handle8,  NULL, parallel_merge, (void*) (&block2) );
	pthread_create( &handle9,  NULL, parallel_merge, (void*) (&block3) );
	pthread_create( &handle10, NULL, parallel_merge, (void*) (&block4) );
	
	pthread_join(handle7,  NULL); 
	pthread_join(handle8,  NULL);
	pthread_join(handle9,  NULL);
	pthread_join(handle10, NULL);
	
	free(temp_arr);	
}

void mergeSortParallel (const int* values, unsigned int N, int* sorted) {
	
	if (N>>30 == 1){
		merge_sort_parallel(&values[0], N/2, &sorted[0]);
		merge_sort_parallel(&values[N/2], N/2, &sorted[N/2]);
		merge(sorted, 0, N/2, N);
	}
	else{
		merge_sort_parallel(values, N, sorted);
	}
	
	//merge_sort_parallel(values, N, sorted);
	
}
