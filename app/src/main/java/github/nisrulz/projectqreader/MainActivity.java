/*
 * Copyright (C) 2016 Nishant Srivastava
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package github.nisrulz.projectqreader;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import github.nisrulz.qreader.QRDataListener;
import github.nisrulz.qreader.QREader;

public class MainActivity extends AppCompatActivity {
  // UI
  private TextView text;

  // QREader
  private SurfaceView mySurfaceView;
  private QREader qrEader;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    text = (TextView) findViewById(R.id.code_info);

    final Button stateBtn = (Button) findViewById(R.id.btn_start_stop);
    // change of reader state in dynamic
    stateBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (qrEader.isCameraRunning()) {
          stateBtn.setText("Start QREader");
          qrEader.stop();
        }
        else {
          stateBtn.setText("Stop QREader");
          qrEader.start();
        }
      }
    });

    stateBtn.setVisibility(View.VISIBLE);

    Button restartbtn = (Button) findViewById(R.id.btn_restart_activity);
    restartbtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        startActivity(new Intent(MainActivity.this, MainActivity.class));
        finish();
      }
    });


    // Setup SurfaceView
    // -----------------
    mySurfaceView = (SurfaceView) findViewById(R.id.camera_view);

    // Init QREader
    // ------------
    qrEader = new QREader.Builder(this, mySurfaceView, new QRDataListener() {
      @Override
      public void onDetected(final String data) {
        Log.d("QREader", "Value : " + data);
        text.post(new Runnable() {
          @Override
          public void run() {
            text.setText(data);
          }
        });
      }
    }).facing(QREader.BACK_CAM)
        .enableAutofocus(true)
        .height(mySurfaceView.getHeight())
        .width(mySurfaceView.getWidth())
        .build();
  }

  @Override
  protected void onResume() {
    super.onResume();

    // Init and Start with SurfaceView
    // -------------------------------
    qrEader.initAndStart(mySurfaceView);
  }

  @Override
  protected void onPause() {
    super.onPause();

    // Cleanup in onPause()
    // --------------------
    qrEader.releaseAndCleanup();
  }
}
