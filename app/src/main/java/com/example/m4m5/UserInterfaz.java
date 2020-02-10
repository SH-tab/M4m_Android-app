package com.example.m4m5;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class UserInterfaz extends AppCompatActivity {


    Button mBtnAdelante, mBtnAtras, mBtnDesconectar ;
    //Button mBtnDerecha, mBtnIzquierda ;
    TextView mTvBufferIn, mTv_seekB_volt, mTv_seekB_stop, mTv_seekB_volante_Izq, mTv_seekB_volante_Dch ;
    SeekBar mSeekB_volt, mSeekB_stop, mSeekB_volante ;
    Handler bluetoothIn ;
    int progressVolt = 0, progressStop = 0, progressVolante = 45 ;

    final int handlerState = 0 ;
    private BluetoothAdapter btAdapter = null ;
    private BluetoothSocket btSocket = null ;
    private StringBuilder DataStringIN = new StringBuilder() ;
    private ConnectedThread MyConexionBT ;

    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") ;

    private static String address = null ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState) ;


        setContentView(R.layout.activity_user_interfaz) ;




        //Enlaza los controles con sus respectivas vistas

        mBtnAdelante = findViewById(R.id.BtnAdelante) ;
        mBtnAtras = findViewById(R.id.BtnAtras) ;

        /*mBtnDerecha = findViewById(R.id.BtnDerecha) ;
        mBtnIzquierda = findViewById(R.id.BtnIzquierda) ;*/

        mBtnDesconectar = findViewById(R.id.BtnDesconectar) ;
        mTvBufferIn = findViewById(R.id.TvBufferIn) ;

        mSeekB_volt = findViewById(R.id.seekB_volt) ;
        mSeekB_volt.setProgress(progressVolt);
        mSeekB_volt.setMax(255);

        mSeekB_stop = findViewById(R.id.seekB_stop) ;
        mSeekB_stop.setProgress(progressStop);
        mSeekB_stop.setMax(10);

        mSeekB_volante = findViewById(R.id.seekB_volante) ;
        mSeekB_volante.setProgress(progressVolante);
        mSeekB_volante.setMax(90);

        mTv_seekB_volt = findViewById(R.id.Tv_seekB_volt) ;
        mTv_seekB_volt.setText("" + progressVolt);

        mTv_seekB_stop = findViewById(R.id.Tv_seekB_stop) ;
        mTv_seekB_stop.setText("" + progressStop);

        mTv_seekB_volante_Izq = findViewById(R.id.Tv_seekB_volante_Izq) ;
        mTv_seekB_volante_Izq.setText("");

        mTv_seekB_volante_Dch = findViewById(R.id.Tv_seekB_volante_Dch) ;
        mTv_seekB_volante_Dch.setText("");

        bluetoothIn = new Handler() {

            public void handleMessage(android.os.Message msg) {

                if (msg.what == handlerState) {

                    String readMessage = (String) msg.obj ;
                    DataStringIN.append(readMessage) ;

                    int endOfLineIndex = DataStringIN.indexOf("#") ;

                    if (endOfLineIndex > 0) {

                        String dataInPrint = DataStringIN.substring(0, endOfLineIndex) ;
                        mTvBufferIn.setText(getString(R.string.Txt_datoMandado) + dataInPrint) ;
                        DataStringIN.delete(0, DataStringIN.length()) ;

                    }

                }
            }

        };



        btAdapter = BluetoothAdapter.getDefaultAdapter(); // get Bluetooth adapter

        VerificarEstadoBT();



        // Configuracion onClick listeners para los botones
        // para indicar que se realizara cuando se detecte el evento Click


        mSeekB_volt.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                progressVolt = i ;
                String datoVolt = String.valueOf(progressVolt);
                mTv_seekB_volt.setText("" +progressVolt);

                MyConexionBT.write(datoVolt) ;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



        mSeekB_stop.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int j, boolean b) {

                progressStop = j ;
                String datoStop = String.valueOf(progressStop) ;
                mTv_seekB_stop.setText("" + progressStop);

                MyConexionBT.write(datoStop);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



        mSeekB_volante.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int k, boolean b) {

                progressVolante = k ;
                String datoVolante = String.valueOf(progressVolante);

                if (progressVolante == 45){

                    mTv_seekB_volante_Dch.setText("Dch : 0");
                    mTv_seekB_volante_Izq.setText("Izq : 0");

                }

                if (progressVolante  < 45) {

                    int nProgressIzq = 45-progressVolante ;

                    mTv_seekB_volante_Dch.setText("Dch : 0");
                    mTv_seekB_volante_Izq.setText("Izq : " + nProgressIzq);

                }

                if (progressVolante  > 45) {

                    int nProgressDch = progressVolante-45 ;

                    mTv_seekB_volante_Dch.setText("Dch : " + nProgressDch);
                    mTv_seekB_volante_Izq.setText("Izq : 0");


                }
                MyConexionBT.write(datoVolante) ;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        mBtnAdelante.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                MyConexionBT.write("A") ;

            }

        });




        mBtnAtras.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                MyConexionBT.write("R") ;

            }

        });



        /*mBtnDerecha.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                MyConexionBT.write("D") ;

            }

        });



        mBtnIzquierda.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                MyConexionBT.write("I") ;

            }

        });    */



        mBtnDesconectar.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                if (btSocket!=null) {

                    try {

                        btSocket.close();

                    } catch (IOException e) {

                        Toast.makeText(getBaseContext(), "Error", Toast.LENGTH_SHORT).show();

                    }
                }

                finish();
            }
        });


    }



    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        //crea un conexion de salida segura para el dispositivo
        //usando el servicio UUID

        return device.createRfcommSocketToServiceRecord(BTMODULEUUID) ;

    }



    @Override
    public void onResume() {

        super.onResume() ;


        //Consigue la direccion MAC desde DeviceListActivity via intent

        Intent intent = getIntent() ;


        //Consigue la direccion MAC desde DeviceListActivity via EXTRA

        address = intent.getStringExtra(DispositivosBT.EXTRA_DEVICE_ADDRESS) ;


        //Setea la direccion MAC

        BluetoothDevice device = btAdapter.getRemoteDevice(address) ;


        try {

            btSocket = createBluetoothSocket(device) ;

        } catch (IOException e) {

            Toast.makeText(getBaseContext(), "La creacción del Socket fallo", Toast.LENGTH_LONG).show();

        }


        // Establece la conexión con el socket Bluetooth.

        try {

            btSocket.connect() ;

        } catch (IOException e) {

            try {

                btSocket.close() ;

            } catch (IOException e2) {

            }

        }


        MyConexionBT = new ConnectedThread(btSocket) ;
        MyConexionBT.start() ;

    }



    @Override
    public void onPause() {

        super.onPause();

        try {

            // Cuando se sale de la aplicación esta parte permite
            // que no se deje abierto el socket

            btSocket.close() ;

        } catch (IOException e2) {

        }

    }



    //Comprueba que el dispositivo Bluetooth Bluetooth está disponible y solicita que se active si está desactivado

    private void VerificarEstadoBT() {

        if(btAdapter==null) {

            Toast.makeText(getBaseContext(), "El dispositivo no soporta bluetooth", Toast.LENGTH_LONG).show() ;

        } else {

            if (btAdapter.isEnabled()) {

            } else {

                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE) ;
                startActivityForResult(enableBtIntent, 1) ;

            }

        }

    }



    //Crea la clase que permite crear el evento de conexion

    private class ConnectedThread extends Thread {

        private final InputStream mmInStream ;
        private final OutputStream mmOutStream ;

        public ConnectedThread(BluetoothSocket socket) {

            InputStream tmpIn = null ;
            OutputStream tmpOut = null ;


            try {

                tmpIn = socket.getInputStream() ;
                tmpOut = socket.getOutputStream() ;

            } catch (IOException e) {

            }


            mmInStream = tmpIn ;
            mmOutStream = tmpOut ;

        }



        public void run() {

            byte[] buffer = new byte[256] ;
            int bytes ;


            // Se mantiene en modo escucha para determinar el ingreso de datos

            while (true) {

                try {

                    bytes = mmInStream.read(buffer) ;
                    String readMessage = new String(buffer, 0, bytes) ;


                    // Envia los datos obtenidos hacia el evento via handler

                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget() ;

                } catch (IOException e) {

                    break ;

                }

            }

        }



        //Envio de trama

        public void write(String input) {

            try {

                mmOutStream.write(input.getBytes()) ;

            }
            catch (IOException e) {

                //si no es posible enviar datos se cierra la conexión

                Toast.makeText(getBaseContext(), "La Conexión fallo", Toast.LENGTH_LONG).show() ;
                finish() ;

            }

        }



    }


}
