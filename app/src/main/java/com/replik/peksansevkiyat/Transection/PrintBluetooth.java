package com.replik.peksansevkiyat.Transection;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.replik.peksansevkiyat.DataClass.ModelDto.Label.ShippingPrintLabelDto;
import com.replik.peksansevkiyat.DataClass.ModelDto.Label.ZarfLabel;
import com.replik.peksansevkiyat.DataClass.ModelDto.Label.ZarfProducts;
import com.replik.peksansevkiyat.DataClass.ModelDto.OrderDetail.OrderPrintLabelDto;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import java.util.List;
import java.util.Set;
import java.util.UUID;


public class PrintBluetooth extends AppCompatActivity {

    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;

    // needed for communication to bluetooth device / network
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;

    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;
    byte FONT_TYPE;

    public static String printer_id;

    public PrintBluetooth() {
    }

    public void findBT() {
        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                Toast.makeText(this, "Yazıcı Bulunamadı", Toast.LENGTH_SHORT).show();
            }
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
            }
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    if (device.getName().equals(printer_id)) {
                        mmDevice = device;
                        break;
                    } else {
                        mmDevice = null;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // tries to open a connection to the bluetooth printer device
    public void openBT() throws IOException {
        try {
            // Standard SerialPortService ID
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();
            beginListenForData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void printQrCode(Bitmap qRBit) {
        try {
            PrintPic printPic1 = PrintPic.getInstance();
            printPic1.init(qRBit);
            byte[] bitmapdata2 = printPic1.printDraw();
            mmOutputStream.write(bitmapdata2);
            //mmOutputStream.write("\n".getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void printPalletLabel(String barcode) {
        final String pdfUrl = GlobalVariable.apiPdfUrl.concat("api/Generate/Palet?paletNo=" + barcode);

        try {
            String printData = "SIZE 75 mm,75 mm\nGAP 0 mm,0 mm\nCLS" +
                    "\nTEXT 70 mm,30 mm,\"3\",0,1.5 mm,1.5 mm,\"" + barcode + "\"" +
                    "\nQRCODE 70 mm,80 mm,\"1\",11,1,0,1,1,\"" + pdfUrl + "\"" +
                    "\nPRINT 1\nEND\n";
            mmOutputStream.write(toTurkish(printData).getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void printOrderLabel(OrderPrintLabelDto orderPrintLabelDto) {
        try {

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void printZarfTable(List<ZarfProducts> products) {
        try {
            final int labelWidth = 75 * 8 - 20 * 2;

            final List<List<ZarfProducts>> slices = this.divideArray(products, 10);
            final List<String> commands = new ArrayList<>();

            commands.add("SIZE 75 mm,75 mm");
            commands.add("GAP 0,0");

            for (int i = 0; i < slices.size(); i++) {
                int positionY = 0;

                commands.add("CLS");
                commands.add("TEXT 20,40,\"3\",0,1,1,\"S.Kodu\"");
                commands.add("TEXT 200,40,\"3\",0,1,1,\"Renk/Logo\"");
                commands.add("TEXT 470,40,\"3\",0,1,1,\"Miktar\"");

                commands.add("BAR 20,80," + labelWidth + ",5");

                positionY = 110;
                for (int j = 0; j < slices.get(i).size(); j++) {
                    commands.add("TEXT 20," + positionY + ",\"3\",0,0.9,0.9,\"" + slices.get(i).get(j).getStokkodu() + "\"");
                    commands.add("TEXT 200," + positionY + ",\"3\",0,0.9,0.9,\"" + slices.get(i).get(j).getRenkLogo() + "\"");
                    commands.add("TEXT 500," + positionY + ",\"3\",0,0.9,0.9,\"" + slices.get(i).get(j).getMiktar() + "\"");

                    positionY += 40;
                }

                commands.add("PRINT 1");
            }

            commands.add("END");

            mmOutputStream.write(toTurkish(String.join("\n", commands)).getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void printZarfHeader(ZarfLabel label) {
        try {
            int totalWidth = 75 * 8 - 20 * 2;
            int y = 0;

            final String pdfUrl = GlobalVariable.apiPdfUrl.concat("api/Generate/Zarf?sevkNo=" + label.getSevkNo());
            final String deliveryName = this.getTruncatedString(label.getTeslimAdi(), 30);
            final String shippingType = this.getTruncatedString(label.getNakliyeTipi(), 30);
            final List<String> deliveryAddressList = this.splitString(label.getTeslimAdresi(), 36);
            final List<String> descriptionList = this.splitString("Not: " + label.getNot(), 45);

            List<String> commands = new ArrayList<>();

            commands.add("SIZE 75 mm,75 mm");
            commands.add("GAP 0,0");
            commands.add("CLS");

            commands.add("TEXT 20,20,\"3\",0,1.5,1.5,\"{deliveryName}\"");
            commands.add("TEXT 20,65,\"3\",0,1.5,1.5,\"{shippingType}\"");

            y = 80;

            for (int i = 0; i < deliveryAddressList.size(); i++) {
                y += (int) (i + 16 * 1.25 + 10);

                final String command = "TEXT 20,{y},\"3\",0,1.25,1.25,\"{text}\""
                        .replace("{y}", String.valueOf(y))
                        .replace("{text}", deliveryAddressList.get(i));

                commands.add(command);
            }

            y += 16;

            for (int i = 0; i < descriptionList.size(); i++) {
                y += (int) (i + 16 + 10);

                final String command = "TEXT 20,{y},\"3\",0,1,1,\"{text}\""
                        .replace("{y}", String.valueOf(y))
                        .replace("{text}", descriptionList.get(i));

                commands.add(command);
            }

            y += 60;

            commands.add("QRCODE 170," + y + ",\"1\",8,1,0,1,1,\"{pdfUrl}\"");

            commands.add("PRINT 1");
            commands.add("END");

            final String raw = String.join("\n", commands)
                    .replace("{deliveryName}", deliveryName)
                    .replace("{shippingType}", shippingType)
                    .replace("{pdfUrl}", pdfUrl);

            mmOutputStream.write(toTurkish(raw).getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void printTestLabel(ShippingPrintLabelDto shippingPrintLabelDto) {
        try {
            final String pdfUrl = GlobalVariable.apiPdfUrl.concat("api/Generate/Zarf?sevkNo=" + shippingPrintLabelDto.shippingNo);

            List<String> commands = new ArrayList<>();
            List<String> deliveryAddress = this.splitString(shippingPrintLabelDto.deliveryAddress, 45);

            int totalHeight = 0;

            commands.add("SIZE 75 mm,75 mm");
            commands.add("GAP 0,0");
            commands.add("CLS");
            commands.add("TEXT 10,48,\"3\",0,1.5,1.5,\"{deliveryName}\"");
            totalHeight += 60;
            for (int i = 0; i < deliveryAddress.size(); i++) {
                totalHeight += 28;
                final String text = "TEXT 10,{height},\"3\",0,1,1,\"{text}\""
                        .replace("{height}", String.valueOf(totalHeight))
                        .replace("{text}", deliveryAddress.get(i));

                commands.add(text);
            }
            totalHeight += 50;
            commands.add("TEXT " + String.valueOf(300 - (shippingPrintLabelDto.shippingNo.length() * 12)) + ", " + totalHeight + ",\"3\",0,1.5,1.5,\"{deliveryNo}\"");
            totalHeight += 40;
            commands.add("QRCODE 90," + totalHeight + ",\"1\",11,1,0,1,1,\"{deliveryPdfUrl}\"");
            commands.add("PRINT 1");
            commands.add("END");

            final String raw = String.join("\n", commands)
                    .replace("{deliveryName}", getTruncatedString(shippingPrintLabelDto.deliveryName, 27))
                    .replace("{deliveryAddress}", shippingPrintLabelDto.deliveryName)
                    .replace("{deliveryPdfUrl}", pdfUrl)
                    .replace("{deliveryNo}", shippingPrintLabelDto.shippingNo);


            mmOutputStream.write(toTurkish(raw).getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String toTurkish(String text) {
        return text.replace("İ", "I")
                .replace("ı", "i")
                .replace("Ö", "O")
                .replace("ö", "o")
                .replace("Ü", "U")
                .replace("ü", "u")
                .replace("Ş", "S")
                .replace("ş", "s")
                .replace("Ç", "C")
                .replace("ç", "c")
                .replace("ğ", "g")
                .replace("Ğ", "G");
    }

    public List<String> splitString(String input, int length) {
        List<String> parts = new ArrayList<>();
        if (input.length() > length) {
            int startIndex = 0;
            while (startIndex < input.length()) {
                int endIndex = Math.min(startIndex + length, input.length());
                parts.add(input.substring(startIndex, endIndex));
                startIndex = endIndex;
            }
        } else {
            parts.add(input);
        }

        return parts;
    }

    public <T> List<List<T>> divideArray(List<T> array, int chunkSize) {
        List<List<T>> dividedArrays = new ArrayList<>();

        for (int i = 0; i < Math.ceil((double) array.size() / chunkSize); i++) {
            int startIndex = i * chunkSize;
            int endIndex = Math.min(startIndex + chunkSize, array.size());

            List<T> sublist = array.subList(startIndex, endIndex);
            dividedArrays.add(sublist);
        }

        return dividedArrays;
    }

    public String getTruncatedString(String input, int length) {
        if (input.length() > length) {
            return input.substring(0, length - 3).concat("...");
        }

        return input;
    }

    public void printOrderLabel(String code) {
        try {
            mmOutputStream.write(code.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void write(byte[] data) {
        try {
            mmOutputStream.write(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void printText(String str) {
        try {
            //String text = str + "\n";
            mmOutputStream.write(str.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * after opening a connection to bluetooth printer device,
     * we have to listen and check if a data were sent to be printed.
     */
    public void beginListenForData() {
        try {
            final Handler handler = new Handler();
            // this is the ASCII code for a newline character
            final byte delimiter = 10;
            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];
            workerThread = new Thread(new Runnable() {
                public void run() {
                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                        try {
                            int bytesAvailable = mmInputStream.available();
                            if (bytesAvailable > 0) {
                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);
                                for (int i = 0; i < bytesAvailable; i++) {
                                    byte b = packetBytes[i];
                                    if (b == delimiter) {
                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(
                                                readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length
                                        );
                                        // specify US-ASCII encoding
                                        final String data = new String(encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;
                                        // tell the user data were sent to bluetooth printer device
                                        handler.post(new Runnable() {
                                            public void run() {
//                                                myLabel.setText(data);
                                            }
                                        });
                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }
                        } catch (IOException ex) {
                            stopWorker = true;
                        }
                    }
                }
            });
            workerThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //    this will update data printer name in ModelUser
    // close the connection to bluetooth printer.
    public void closeBT() throws IOException {
        try {
            stopWorker = true;
            mmOutputStream.close();
            mmInputStream.close();
            mmSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
