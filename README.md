# 64-bit-UUID-Generator
A node in the distributed UUID generation system designed for scalability

Specifications:
 * 64-bit UUID generator
 * The distributed system has 1024 nodes with nodeIds: 0-1023
 * Each node receives up to 100000 requests per second

Design:
 * UUID structure: Time Stamp + Node ID + Counter
 * Handle up to 128 requests per millisecond with vertical scaling of 2x
 *
 *  Counter bits: 8
 *  127 can fit in 7 bits. 
 *  To allow for vertical scaling 1 additional bit is allocated to counter.
 * 	
 * Node ID bits: 13
 * 1023 can fit in 10 bits
 * To allow for horizontal scaling by 8x in the future 3 more bits are allocated 
 * 
 * 
 * Time stamp bits: 43
 * Current time stamp occupies 41 bits. Same 41 bits can handle time stamps for additional ~16 years
 * Two additional bits allow for unique time stamps generation for next ~200 years
 * 
 * Final UUID structure:
 * 		43 bit time stamp + 13 bit node id + 8 bit counter = 64 bits
