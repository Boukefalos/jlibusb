/*
 * testlibusb.c
 *
 *  Test suite program
 */

#include <stdio.h>
#include <string.h>
#include "usb.h"

#define snprintf printf

#define ID_PRODUCT 0x1004	//0x8613
#define ID_VENDOR 0x04B4

#define CONFIGURATION 1
#define INTERFACE 0
#define ALTINTERFACE 0
#define TIMEOUT 2000

#define OUT_ENDPOINT 0x02
#define IN_ENDPOINT 0x86

int verbose = 0;

void print_endpoint(struct usb_endpoint_descriptor *endpoint)
{
  printf("      bEndpointAddress: %02xh\n", endpoint->bEndpointAddress);
  printf("      bmAttributes:     %02xh\n", endpoint->bmAttributes);
  printf("      wMaxPacketSize:   %d\n", endpoint->wMaxPacketSize);
  printf("      bInterval:        %d\n", endpoint->bInterval);
  printf("      bRefresh:         %d\n", endpoint->bRefresh);
  printf("      bSynchAddress:    %d\n", endpoint->bSynchAddress);
}

void print_altsetting(struct usb_interface_descriptor *interface)
{
  int i;

  printf("    bInterfaceNumber:   %d\n", interface->bInterfaceNumber);
  printf("    bAlternateSetting:  %d\n", interface->bAlternateSetting);
  printf("    bNumEndpoints:      %d\n", interface->bNumEndpoints);
  printf("    bInterfaceClass:    %d\n", interface->bInterfaceClass);
  printf("    bInterfaceSubClass: %d\n", interface->bInterfaceSubClass);
  printf("    bInterfaceProtocol: %d\n", interface->bInterfaceProtocol);
  printf("    iInterface:         %d\n", interface->iInterface);

  for (i = 0; i < interface->bNumEndpoints; i++)
    print_endpoint(&interface->endpoint[i]);
}

void print_interface(struct usb_interface *interface)
{
  int i;

  for (i = 0; i < interface->num_altsetting; i++)
    print_altsetting(&interface->altsetting[i]);
}

void print_configuration(struct usb_config_descriptor *config)
{
  int i;

  printf("  wTotalLength:         %d\n", config->wTotalLength);
  printf("  bNumInterfaces:       %d\n", config->bNumInterfaces);
  printf("  bConfigurationValue:  %d\n", config->bConfigurationValue);
  printf("  iConfiguration:       %d\n", config->iConfiguration);
  printf("  bmAttributes:         %02xh\n", config->bmAttributes);
  printf("  MaxPower:             %d\n", config->MaxPower);

  for (i = 0; i < config->bNumInterfaces; i++)
    print_interface(&config->interface[i]);
}

int print_device(struct usb_device *dev, int level)
{
  usb_dev_handle *udev;
  char description[256];
  char string[256];
  int ret, i;

  udev = usb_open(dev);
  if (udev) {
    if (dev->descriptor.iManufacturer) {
      ret = usb_get_string_simple(udev, dev->descriptor.iManufacturer, string, sizeof(string));
      if (ret > 0)
        snprintf(description, sizeof(description), "%s - ", string);
      else
        snprintf(description, sizeof(description), "%04X - ",
                 dev->descriptor.idVendor);
    } else
      snprintf(description, sizeof(description), "%04X - ",
               dev->descriptor.idVendor);

    if (dev->descriptor.iProduct) {
      ret = usb_get_string_simple(udev, dev->descriptor.iProduct, string, sizeof(string));
      if (ret > 0)
        snprintf(description + strlen(description), sizeof(description) -
                 strlen(description), "%s", string);
      else
        snprintf(description + strlen(description), sizeof(description) -
                 strlen(description), "%04X", dev->descriptor.idProduct);
    } else
      snprintf(description + strlen(description), sizeof(description) -
               strlen(description), "%04X", dev->descriptor.idProduct);

  } else
    snprintf(description, sizeof(description), "%04X - %04X",
             dev->descriptor.idVendor, dev->descriptor.idProduct);

  printf("%.*sDev #%d: %s\n", level * 2, "                    ", dev->devnum,
         description);

  if (udev && verbose) {
    if (dev->descriptor.iSerialNumber) {
      ret = usb_get_string_simple(udev, dev->descriptor.iSerialNumber, string, sizeof(string));
      if (ret > 0)
        printf("%.*s  - Serial Number: %s\n", level * 2,
               "                    ", string);
    }
  }

  if (udev)
    usb_close(udev);

  if (verbose) {
    if (!dev->config) {
      printf("  Couldn't retrieve descriptors\n");
      return 0;
    }

    for (i = 0; i < dev->descriptor.bNumConfigurations; i++)
      print_configuration(&dev->config[i]);
  } else {
    for (i = 0; i < dev->num_children; i++)
      print_device(dev->children[i], level + 1);
  }

  return 0;
}

int read(struct usb_dev_handle *handle)
{
	if (usb_claim_interface(handle, INTERFACE) < 0) {
		printf("error on usb_claim_interface: %s\n", usb_strerror());
		return -1;
	}
	printf("usb_claim_interface successful\n");
	if (usb_set_altinterface(handle, ALTINTERFACE) < 0){
		printf("usb_set_altinterface failed: %s\n", usb_strerror());
	}
	
	int size = 512, res;
	char *data = (char *) malloc(size*sizeof(char));
	res = usb_bulk_read(handle, IN_ENDPOINT, data, size, TIMEOUT);
	if (res < 0){
		printf("usb_bulk_read failed: %s\n", usb_strerror());
	}
	printf("usb_bulk_read: %d bytes read: ", res);
	for (int i = 0; i < res; ++i) {
		printf("%3x ", data[i]);
	}
	printf("\n");
	
	usb_release_interface(handle, INTERFACE);
}

int write(struct usb_dev_handle *handle)
{
	int size = 12;
	char *data = (char *) malloc(size*sizeof(char));
	data[0] = 0x33;
	data[1] = 0x5B;
	data[2] = 0x02;
	data[3] = 0x01;
	data[4] = 0x00;
	data[5] = 0x05;
	data[6] = 0x01;
	data[7] = 0x03;
	data[8] = 0x07;
	data[9] = 0x0F;
	data[10] = 0x7F;
	data[11] = 0x1F;
	// data = {0x33, 0x5B, 0x02, 0x01, 0x00, 0x05, 0x01, 0x03, 0x07, 0x0F, 0x7F, 0x1F};

	if (usb_claim_interface(handle, INTERFACE) < 0) {
		printf("error on usb_claim_interface: %s\n", usb_strerror());
		return -1;
	}
	printf("usb_claim_interface successful\n");
	if (usb_set_altinterface(handle, ALTINTERFACE) < 0){
		printf("usb_set_altinterface failed: %s\n", usb_strerror());
	}
	printf("usb_bulk_write: writing %d bytes: ", size);
	for (int i = 0; i < size; ++i) {
		printf("%3x ", data[i]);
	}
	printf("\n");
	
	int res = usb_bulk_write(handle, OUT_ENDPOINT, data, size, TIMEOUT);
	if (res < 0){
		printf("usb_bulk_write failed: %s\n", usb_strerror());
		return -1;
	}
	
	printf("usb_bulk_write: %d bytes written\n", res);
	
	usb_release_interface(handle, INTERFACE);
}

int main(int argc, char *argv[])
{
	struct usb_bus *bus;
	struct usb_device *dev;
	struct usb_dev_handle *handle;
  
	bool run = true;

//  if (argc > 1 && !strcmp(argv[1], "-v"))
//    verbose = 1;

	verbose = 1;

	usb_set_debug(255);
	
	usb_init();

	usb_find_busses();
	usb_find_devices();

//  for (bus = usb_get_busses(); bus; bus = bus->next) {
//    if (bus->root_dev && !verbose)
//      print_device(bus->root_dev, 0);
//    else {
//      struct usb_device *dev;
//
//      for (dev = bus->devices; dev; dev = dev->next)
//        print_device(dev, 0);
//    }
//  }

	int size = 512;
	char *data = (char *) malloc(size*sizeof(char));


	for (bus = usb_get_busses(); bus; bus = bus->next) {
		for (dev = bus->devices; dev; dev = dev->next) {
			if ((dev->descriptor.idProduct == ID_PRODUCT) && (dev->descriptor.idVendor == ID_VENDOR)){
				printf("Device found\n");
				handle = usb_open(dev);
				if (!handle) {
					printf("invalid handle: %s\n", usb_strerror());
					system("PAUSE");
					return -1;
				}
				if (usb_set_configuration(handle, CONFIGURATION) < 0) {
	  				printf("error on usb_set_configuration: %s\n", usb_strerror());
	  				system("PAUSE");
	  				return -1;
	  			}
	  			
	  			printf("w=write, r=read, x=exit, t=write+read:\n");
	
	 			while (run) {
		  			scanf("%s", data);
		  			
		    		switch (data[0]) {
						case 'w':	// write
							write(handle);
							break;
						case 'r':	// read
							read(handle);
							break;
						case 'x':	// exit
							run = false;
							break;
						case 't':	// write + read
							if (write(handle)) {
								read(handle);
							}
							break;
						default:
							break;
					}
	
	//	  			printf("type a string...\n");
	//	    		scanf("%s", data);	// Get a string
		    		
	//	  			if (usb_claim_interface(handle, INTERFACE) < 0) {
	//	  				printf("error on usb_claim_interface: %s\n", usb_strerror());
	//	  				system("PAUSE");
	//	  				return -1;
	//	  			}
	//	  			printf("usb_claim_interface successful\n");
	//	  			if (usb_set_altinterface(handle, ALTINTERFACE) < 0){
	//	  				printf("usb_set_altinterface failed: %s\n", usb_strerror());
	//	  			}
	//	 			
	//	  			if (usb_bulk_write(handle, OUT_ENDPOINT, data, strlen(data), 3000) < 0){
	//	   				printf("usb_bulk_write failed: %s\n", usb_strerror());
	//	   				system("PAUSE");
	//	  				return -1;
	//	 			}
	//	 			
	//	    		strcpy(data, "12345678901234567890");
	//	    		printf("%s\n", "read data");
	//	 			if (usb_bulk_read(handle, IN_ENDPOINT, data, size, 3000) < 0){
	//	   				printf("usb_bulk_read failed: %s\n", usb_strerror());
	//				}
	//				printf("output %d, %s\n", size, data);
	//	//			for (int i = 0; i < size; ++i) {
	//	//				printf("%4x ", data[i]);
	//	//			}
	//	  			
	//	  			usb_release_interface(handle, INTERFACE);
	// 			}
	//
	//  			usb_close(handle);
		  		}
		  		printf("\ndone\n");
		  	}
	  	}
	}
	system("PAUSE");

	return 1;
}
