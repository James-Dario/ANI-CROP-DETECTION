package com.example.camera1;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaSession2Service;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.camera1.ml.Model;

import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;
import org.tensorflow.lite.DataType;

import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    ImageButton camera, gallery, back;
    ImageView imageView;
    TextView result,description;
    int imageSize = 224;
    String str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();

        str = intent.getStringExtra("plantkey");


        camera = findViewById(R.id.button);
        gallery = findViewById(R.id.button2);
        back = findViewById(R.id.backbutton);
        result = findViewById(R.id.result);
        imageView = findViewById(R.id.imageView);
        description = findViewById(R.id.description);



        back.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SelectionScreen.class));
                finish();
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if(checkSelfPermission(Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED){
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent,3);
                }else{
                    requestPermissions(new String[]{Manifest.permission.CAMERA},100);
                }
            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(cameraIntent, 1);
            }
        });
    }

    public void classifyImage(Bitmap image, String plantstr){

        try {
            Model model = Model.newInstance(getApplicationContext());
            Bitmap input = Bitmap.createScaledBitmap(image,224,224,false);

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);

            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            int[] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
            int pixel = 0;
            //iterate over each pixel and extract R, G, and B values. Add those values individually to the byte buffer.
            for(int i = 0; i < imageSize; i ++){
                for(int j = 0; j < imageSize; j++){
                    int val = intValues[pixel++]; // RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 1));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 1));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 1));
                }
            }


            /*TensorImage tbuffer = new TensorImage(DataType.FLOAT32);
            tbuffer.load(image);*/

            //ByteBuffer byteBuffer = tbuffer.getBuffer();

            inputFeature0.loadBuffer(byteBuffer);
            // Runs model inference and gets result.
            Model.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float [] confidences = outputFeature0.getFloatArray();

            String classes[] = {"banana_Cordana",
                    "banana_healthy",
                    "banana_pestalotiopsis",
                    "banana_sigatoka",
                    "cabbage_Backmoth",
                    "cabbage_leafminer",
                    "cabbage_mildew",
                    "coffee_miner",
                    "coffee_rust",
                    "corn_Blight",
                    "corn_Common_Rust",
                    "corn_Gray_Leaf_Spot",
                    "corn_Healthy",
                    "cotton_bacterial_blight",
                    "cotton_curl_virus",
                    "cotton_fussarium_wilt",
                    "cotton_healthy",
                    "NOT_A_CROP",
                    "rice_Bacterial_leaf_blight",
                    "rice_Blast",
                    "rice_Brown spot",
                    "rice_healthy",
                    "rice_Leaf smut",
                    "rice_Tungro",
                    "sugarcane_Bacterial Blight",
                    "sugarcane_Healthy",
                    "sugarcane_Red Rot",
                    "tomato_Bacterial_spot",
                    "tomato_Early_blight",
                    "tomato_healthy",
                    "tomato_Late_blight",
                    "tomato_Leaf_Mold",
                    "tomato_Septoria_leaf_spot",
                    "tomato_Target_Spot",
                    "tomato_Tomato_mosaic_virus",
                    "tomato_Tomato_YellowLeaf_Curl_Virus"};

            String classesformat[] = {"Banana Cordana",
                    "Banana Healthy",
                    "Banana Pestalotiopsis",
                    "Banana Sigatoka",
                    "Cabbage Backmoth",
                    "Cabbage Leafminer",
                    "Cabbage Mildew",
                    "Coffee Miner",
                    "Coffee Rust",
                    "Corn Blight",
                    "Corn Common Rust",
                    "Corn Gray Leaf Spot",
                    "Corn Healthy",
                    "Cotton Bacterial Blight",
                    "Cotton Curl Virus",
                    "Cotton Fussarium Wilt",
                    "Cotton Healthy",
                    "NOT_A_CROP",
                    "Rice Bacterial Leaf Blight",
                    "Rice Blast",
                    "Rice Brown Spot",
                    "Rice Healthy",
                    "Rice Leaf Smut",
                    "Rice Tungro",
                    "Sugarcane Bacterial Blight",
                    "Sugarcane Healthy",
                    "Sugarcane Red Rot",
                    "Tomato Bacterial Spot",
                    "Tomato Early Blight",
                    "Tomato Healthy",
                    "Tomato Late Blight",
                    "Tomato Leaf Mold",
                    "Tomato Septoria Leaf Spot",
                    "Tomato Target Spot",
                    "Tomato Mosaic Virus",
                    "Tomato YellowLeaf Curl Virus"};


            int fpos =0;
            int spos =0;
            int tpos = 0;
            float fcon=0;
            float scon=0;
            float tcon = 0;


            for(int i=0; i<confidences.length; i++){
                if(fcon <= confidences[i]){
                    tcon = scon;
                    scon = fcon;
                    fcon = confidences[i];
                    tpos = spos;
                    spos = fpos;
                    fpos = i;
                }else if(scon <= confidences[i]){
                    tcon = scon;
                    scon = confidences[i];
                    tpos = spos;
                    spos = i;
                }else if(tcon <= confidences[i]){
                    tcon = confidences[i];
                    tpos = i;
                }
            }

            //result.setText(classes[17]);
            //result.setText(classes[fpos]);
            //description.setText(classes[fpos]+" " + classes[spos] +" " +classes[tpos]);
            //result.setText(classes[3]);

            String[] classdel1 = classes[fpos].split("_");
            String[] classdel2 = classes[spos].split("_");
            String[] classdel3 = classes[tpos].split("_");

            int toppos[] = {fpos,spos,tpos};
            String topthree[] = {classdel1[0],classdel2[0],classdel3[0]};





            for(int i=0; i<topthree.length; i++){
                if(topthree[i].equals(plantstr)){
                    result.setText(classesformat[toppos[i]]);
                    switch (toppos[i]){
                        case 0:
                            description.setText("Ang mga apektadong parte o dahon ay kailangan tanggalin.\n" +
                                    " Kung kinakailangan ay gamitan ng fungicide.\n");
                            break;
                        //Healthy
                        case 1:
                            description.setText("Walang sakit ang halaman.");
                            break;
                        //Banana Pestalotiopsis
                        case 2:
                            description.setText("Gamitan ng fungicide.");
                            break;
                        //Banana Sigatoka
                        case 3:
                            description.setText("Ang mga apektadong parte o dahon ay kailangan tanggalin.\n" +
                                    " Kung kinakailangan ay gamitan ng fungicide.\n");
                            break;
                        //Cabbage Backmoth
                        case 4:
                            description.setText("Tanggalin ang mga peste, ilagay ito sa tubig na may sabon upang mapatay. \n" +
                                    "Maari din gumamit sng organic na pesticide.\n");
                            break;
                        //Cabbage Leafminer
                        case 5:
                            description.setText("Kung ang bilang ng leafminer ay mababa, maaaring tanggaling ang mga apektadong parte o dahon, \n" +
                                    "O kaya sirain na mismo ang halaman.\n");
                            break;
                        //Cabbage Mildew
                        case 6:
                            description.setText("Tanggalin ang mga nakapalibot sa apektadong halaman na maaaring paglipatan ng mildew.\n" +
                                    "Kung maaari, sunugin at ilibing ang mga pananim tuwing ani.\n");
                            break;
                        //Coffee Miner
                        case 7:
                            description.setText("Wag ilipat o gamiting muli ang lupang pinag-taniman ng apektadong halaman.\n" +
                                    "Huwag nang gamitin o liinising mabuti ang mga kagamitan na ginamit sa apekatdong lupa’t halaman.\n" +
                                    "Huwag itanim ang halaman sa lupa kung saan ito in-ani.\n" +
                                    "Gamitan ng pesticide.\n");
                            break;
                        //Coffee Rust
                        case 8:
                            description.setText("Linising mabuti ang mga kagamitan na nagamit sa ibang taniman.\n" +
                                    "Maaaring gamitan ng copper-based fungicides.\n");
                            break;
                        //Corn Blight
                        case 9:
                            description.setText("Huwag itanim ang mais sa lupang pinagtaniman pagkatapos ng isang ani.\n" +
                                    "Itanim ang mais sa mas maluwag na lugar upang maiwasan ang mahalumigmigan ang tanim.\n" +
                                    "Gumamit lamang fungicides sa mga hindi inaasahang pangyayari.\n");
                            break;
                        //Corn Common Rust
                        case 10:
                            description.setText("Maaring gumamit ng fungicides sa pag tangal ng common rust.\n" +
                                    "Kung ang tanim ay sweet corn, maaaring gumamit ng foliar fungicides. \n" +
                                    "At tuwing magtatanim ay ugaliing sirain ang mga volunteer maize bago mag tanim ng bagong pananim.\n");
                            break;
                        //Corn Gray Leaf Spot
                        case 11:
                            description.setText("Huwag itanim ang mais sa lupang pinagtaniman pagkatapos ng isang ani.\n" +
                                    "Gumamit ng mga foliar fungicides.\n");
                            break;
                        //Corn Healthy
                        case 12:
                            description.setText("Walang sakit ang halaman.");
                            break;
                        case 13:
                            description.setText("Patuyuin ang mga dahon ng bulak upang maiwasan ang pagbuo ng blight.");
                            break;
                        case 14:
                            description.setText("Tanggalin ang mga nakapalibot na apektadong halaman.\n" +
                                    "Magtanim ng higit sa isang uri ng bulak.\n" +
                                    "Tutukan ang irigasyon tuwing tag-init.\n");
                            break;
                        case 15:
                            description.setText("Ang mga apektadong parte o dahon ay kailangan tanggalin.\n" +
                                    "Linising mabuti ang mga kagamitan na nagamit sa mga apektadong taniman.\n");
                            break;
                        case 16:
                            description.setText("Walang sakit ang halaman.");
                            break;
                        case 17:
                            description.setText("Hindi ito halaman.");
                            break;
                        case 18:
                            description.setText("Linisin ang mga binhi gamit ang mainit na tubig.\n" +
                                    "Panatilihing malinis ang palayan, tanggalin ang mga ligaw na halaman.\n" +
                                    "Alisan ng tubig ang palayan pagkatapos ng baha.\n" +
                                    "Maaaring gamitan ng copper-based fungicides.\n");
                            break;
                        case 19:
                            description.setText("Maaaring gamitan ng copper-based fungicides at antibiotics.\n" +
                                    "Panatilihing malinis ang palayan, tanggalin ang mga ligaw na halaman.\n");
                            break;
                        case 20:
                            description.setText("Panatilihing malinis ang palayan, tanggalin ang mga ligaw na halaman.\n" +
                                    "Balansehin ang nutrisyon ng palayan.\n");
                            break;
                        case 21:
                            description.setText("Walang sakit ang halaman.");
                            break;
                        case 22:
                            description.setText("Gamitan ng chemical pesticide.");
                            break;
                        case 23:
                            description.setText("Wag magtanim ng mga bagong palay malapit sa lupang apektado.\n" +
                                    "I-monitor nang mabuti ang mga halaman para maiwasan ang berdeng ngusong-kabayo.\n" +
                                    "Kung may makuha, tanggalin ang mga ito at ilagay sa tubig na may sabon upang mapatay.\n" +
                                    "Kung ang bilang ng apektadong palay ay mababa, maaaring tanggalin ang mga ito.\n");
                            break;
                        case 24:
                            description.setText("Maaaring gamitan ang mga binhi ng fungicides.\n" +
                                    "Gumamit ng nitrogen bilang pataba.\n");
                            break;
                        case 25:
                            description.setText("Walang sakit ang halaman.");
                            break;
                        case 26:
                            description.setText("Tanggalin ang apektadong halaman pati na ang mga nakapalibot at sunugin.\n" +
                                    "Sunugin pati ang mga apektadong dahon.\n" +
                                    "Huwag mag-cultivate ng iisang uri ng tubo sa iisang lupa sa loob nang magkakasunod na taon.\n");
                            break;
                        case 27:
                            description.setText("Panatilihing malinis ang taniman, tanggalin ang mga ligaw na halaman.\n" +
                                    "Linising mabuti ang mga gamit na pangtanim.\n" +
                                    "Gumamit ng drip irrigation. Panatilihing tuyo ang mga dahon.\n" +
                                    "Wag hawakan ang mga halaman kapag sila’y basa.\n" +
                                    "Wag “lunurin” ang mga halaman.\n");
                            break;
                        case 28:
                            description.setText("Panatilihing malinis ang taniman, tanggalin ang mga ligaw na halaman.\n" +
                                    "Linising mabuti ang mga gamit na pangtanim.\n" +
                                    "Tanggalin at sirain ang mga halaman na balisa at linising mabuti ang mga ginamit na pantanggal.\n");
                            break;
                        case 29:
                            description.setText("Walang sakit ang iyong halaman.");
                            break;
                        case 30:
                            description.setText("Panatilihing malinis ang taniman, tanggalin ang mga ligaw na halaman at iba pang kalat.\n" +
                                    "I-monitor ang halaman lalo na kapag tag-ulan at tag-lamig.\n" +
                                    "Maaaring gamitan ng copper-based fungicides.\n");
                            break;
                        case 31:
                            description.setText("Panatilihing malinis ang taniman, tanggalin ang mga ligaw na halaman.\n" +
                                    "Linising mabuti ang mga gamit na pangtanim.\n" +
                                    "Panatilihing tuyo ang mga dahon.\n" +
                                    "Linisin ang taniman pagkatapos ng ani.\n");
                            break;
                        case 32:
                            description.setText("Panatilihing malinis ang taniman, tanggalin ang mga ligaw na halaman.\n" +
                                    "Ibahin ang pagtataniman ng halaman kada taon.\n" +
                                    "Maaaring gamitan ng copper-based fungicides.\n");
                            break;
                        case 33:
                            description.setText("Panatilihing malinis ang taniman, tanggalin ang mga ligaw na halaman.\n" +
                                    "Linising mabuti ang mga gamit na pangtanim.\n" +
                                    "Gumamit ng drip irrigation. Panatilihing tuyo ang mga dahon.\n" +
                                    "Wag hawakan ang mga halaman kapag sila’y basa.\n" +
                                    "Wag “lunurin” ang mga halaman.\n");
                            break;
                        case 34:
                            description.setText("Panatilihing malinis ang taniman, tanggalin ang mga ligaw na halaman.\n" +
                                    "Linising mabuti ang mga gamit na pangtanim.\n" +
                                    "Tanggalin at sirain ang mga halaman na balisa at linising mabuti ang mga ginamit na pantanggal.\n");
                            break;
                        case 35:
                            description.setText("Tanggalin ang apektadong halaman at sunugin. Takpan ito ng itim na plastic bag at iwan muna nang 1 hanggang 2 araw bago sunugin.\n" +
                                    "Gumamit ng chemical pesticides o tubig na may halong sabon. I-spray ito sa mga halaman kada linggo upang mapatay ang mga “whiteflies”\n");
                            break;
                        default:
                            //break;
                    }
                    break;
                }else if(confidences[17]>=.50){
                        result.setText("Hindi ito isang halaman");
                        description.setText("Ulitin ang pagkuha ng litrato");
                        break;
                }else{
                    String halaman=" ";
                    switch (plantstr){
                        case "banana":
                            halaman="saging.";
                            break;
                        case "cabbage":
                            halaman="pechay.";
                            break;
                        case "coffee":
                            halaman="kape.";
                            break;
                        case "corn":
                            halaman="mais.";
                            break;
                        case "cotton":
                            halaman="bulak.";
                            break;
                        case "rice":
                            halaman="palay.";
                            break;
                        case "sugarcane":
                            halaman="tubo.";
                            break;
                        case "tomato":
                            halaman="kamatis.";
                            break;
                    }
                    result.setText("Hindi ito isang " + halaman);
                    description.setText("Piliin ang na-angkop na uri ng halaman. Ulitin ang pagkuha ng litrato");
                }
            }





            /*if (plantstr.equals(classdel[0])){
                result.setText(classesformat[maxPos]);
                switch (maxPos){
                    case 0:
                        description.setText("Ang mga apektadong parte o dahon ay kailangan tanggalin.\n" +
                                " Kung kinakailangan ay gamitan ng fungicide.\n");
                        break;
                    //Healthy
                    case 1:
                        description.setText("Walang sakit ang halaman.");
                        break;
                    //Banana Pestalotiopsis
                    case 2:
                        description.setText("Gamitan ng fungicide.");
                        break;
                    //Banana Sigatoka
                    case 3:
                        description.setText("Ang mga apektadong parte o dahon ay kailangan tanggalin.\n" +
                                " Kung kinakailangan ay gamitan ng fungicide.\n");
                        break;
                    //Cabbage Backmoth
                    case 4:
                        description.setText("Tanggalin ang mga peste, ilagay ito sa tubig na may sabon upang mapatay. \n" +
                                "Maari din gumamit sng organic na pesticide.\n");
                        break;
                    //Cabbage Leafminer
                    case 5:
                        description.setText("Kung ang bilang ng leafminer ay mababa, maaaring tanggaling ang mga apektadong parte o dahon, \n" +
                                "O kaya sirain na mismo ang halaman.\n");
                        break;
                    //Cabbage Mildew
                    case 6:
                        description.setText("Tanggalin ang mga nakapalibot sa apektadong halaman na maaaring paglipatan ng mildew.\n" +
                                "Kung maaari, sunugin at ilibing ang mga pananim tuwing ani.\n");
                        break;
                    //Coffee Miner
                    case 7:
                        description.setText("Wag ilipat o gamiting muli ang lupang pinag-taniman ng apektadong halaman.\n" +
                                "Huwag nang gamitin o liinising mabuti ang mga kagamitan na ginamit sa apekatdong lupa’t halaman.\n" +
                                "Huwag itanim ang halaman sa lupa kung saan ito in-ani.\n" +
                                "Gamitan ng pesticide.\n");
                        break;
                    //Coffee Rust
                    case 8:
                        description.setText("Linising mabuti ang mga kagamitan na nagamit sa ibang taniman.\n" +
                                "Maaaring gamitan ng copper-based fungicides.\n");
                        break;
                    //Corn Blight
                    case 9:
                        description.setText("Huwag itanim ang mais sa lupang pinagtaniman pagkatapos ng isang ani.\n" +
                                "Itanim ang mais sa mas maluwag na lugar upang maiwasan ang mahalumigmigan ang tanim.\n" +
                                "Gumamit lamang fungicides sa mga hindi inaasahang pangyayari.\n");
                        break;
                    //Corn Common Rust
                    case 10:
                        description.setText("Maaring gumamit ng fungicides sa pag tangal ng common rust.\n" +
                                "Kung ang tanim ay sweet corn, maaaring gumamit ng foliar fungicides. \n" +
                                "At tuwing magtatanim ay ugaliing sirain ang mga volunteer maize bago mag tanim ng bagong pananim.\n");
                        break;
                    //Corn Gray Leaf Spot
                    case 11:
                        description.setText("Huwag itanim ang mais sa lupang pinagtaniman pagkatapos ng isang ani.\n" +
                                "Gumamit ng mga foliar fungicides.\n");
                        break;
                    //Corn Healthy
                    case 12:
                        description.setText("Walang sakit ang halaman.");
                        break;
                    case 13:
                        description.setText("Patuyuin ang mga dahon ng bulak upang maiwasan ang pagbuo ng blight.");
                        break;
                    case 14:
                        description.setText("Tanggalin ang mga nakapalibot na apektadong halaman.\n" +
                                "Magtanim ng higit sa isang uri ng bulak.\n" +
                                "Tutukan ang irigasyon tuwing tag-init.\n");
                        break;
                    case 15:
                        description.setText("Ang mga apektadong parte o dahon ay kailangan tanggalin.\n" +
                                "Linising mabuti ang mga kagamitan na nagamit sa mga apektadong taniman.\n");
                        break;
                    case 16:
                        description.setText("Walang sakit ang halaman.");
                        break;
                    case 17:
                        description.setText("Hindi ito halaman.");
                        break;
                    case 18:
                        description.setText("Linisin ang mga binhi gamit ang mainit na tubig.\n" +
                                "Panatilihing malinis ang palayan, tanggalin ang mga ligaw na halaman.\n" +
                                "Alisan ng tubig ang palayan pagkatapos ng baha.\n" +
                                "Maaaring gamitan ng copper-based fungicides.\n");
                        break;
                    case 19:
                        description.setText("Maaaring gamitan ng copper-based fungicides at antibiotics.\n" +
                                "Panatilihing malinis ang palayan, tanggalin ang mga ligaw na halaman.\n");
                        break;
                    case 20:
                        description.setText("Panatilihing malinis ang palayan, tanggalin ang mga ligaw na halaman.\n" +
                                "Balansehin ang nutrisyon ng palayan.\n");
                        break;
                    case 21:
                        description.setText("Walang sakit ang halaman.");
                        break;
                    case 22:
                        description.setText("Gamitan ng chemical pesticide.");
                        break;
                    case 23:
                        description.setText("Wag magtanim ng mga bagong palay malapit sa lupang apektado.\n" +
                                "I-monitor nang mabuti ang mga halaman para maiwasan ang berdeng ngusong-kabayo.\n" +
                                "Kung may makuha, tanggalin ang mga ito at ilagay sa tubig na may sabon upang mapatay.\n" +
                                "Kung ang bilang ng apektadong palay ay mababa, maaaring tanggalin ang mga ito.\n");
                        break;
                    case 24:
                        description.setText("Maaaring gamitan ang mga binhi ng fungicides.\n" +
                                "Gumamit ng nitrogen bilang pataba.\n");
                        break;
                    case 25:
                        description.setText("Walang sakit ang halaman.");
                        break;
                    case 26:
                        description.setText("Tanggalin ang apektadong halaman pati na ang mga nakapalibot at sunugin.\n" +
                                "Sunugin pati ang mga apektadong dahon.\n" +
                                "Huwag mag-cultivate ng iisang uri ng tubo sa iisang lupa sa loob nang magkakasunod na taon.\n");
                        break;
                    case 27:
                        description.setText("Panatilihing malinis ang taniman, tanggalin ang mga ligaw na halaman.\n" +
                                "Linising mabuti ang mga gamit na pangtanim.\n" +
                                "Gumamit ng drip irrigation. Panatilihing tuyo ang mga dahon.\n" +
                                "Wag hawakan ang mga halaman kapag sila’y basa.\n" +
                                "Wag “lunurin” ang mga halaman.\n");
                        break;
                    case 28:
                        description.setText("Panatilihing malinis ang taniman, tanggalin ang mga ligaw na halaman.\n" +
                                "Linising mabuti ang mga gamit na pangtanim.\n" +
                                "Tanggalin at sirain ang mga halaman na balisa at linising mabuti ang mga ginamit na pantanggal.\n");
                        break;
                    case 29:
                        description.setText("Tanggalin ang apektadong halaman at sunugin. Takpan ito ng itim na plastic bag at iwan muna nang 1 hanggang 2 araw bago sunugin.\n" +
                                "Gumamit ng chemical pesticides o tubig na may halong sabon. I-spray ito sa mga halaman kada linggo upang mapatay ang mga “whiteflies”\n");
                        break;
                    case 30:
                        description.setText("Panatilihing malinis ang taniman, tanggalin ang mga ligaw na halaman.\n" +
                                "Linising mabuti ang mga gamit na pangtanim.\n" +
                                "Gumamit ng drip irrigation. Panatilihing tuyo ang mga dahon.\n" +
                                "Diligan tuwing umaga para mas mabilis matuyo ang mga dahon.\n" +
                                "Gamitan ng mainit na tubig ang mga binhi.\n");
                        break;
                    case 31:
                        description.setText("Panatilihing malinis ang taniman, tanggalin ang mga ligaw na halaman.\n" +
                                "Maaaring gamitan ng fungicides.\n" +
                                "Panatilihing sapat ang pataba.\n");
                        break;
                    case 32:
                        description.setText("Walang sakit ang halaman");
                        break;
                    case 33:
                        description.setText("Panatilihing malinis ang taniman, tanggalin ang mga ligaw na halaman at iba pang kalat.\n" +
                                "I-monitor ang halaman lalo na kapag tag-ulan at tag-lamig.\n" +
                                "Maaaring gamitan ng copper-based fungicides.\n");
                        break;
                    case 34:
                        description.setText("Panatilihing malinis ang taniman, tanggalin ang mga ligaw na halaman.\n" +
                                "Linising mabuti ang mga gamit na pangtanim.\n" +
                                "Panatilihing tuyo ang mga dahon.\n" +
                                "Linisin ang taniman pagkatapos ng ani.\n");
                        break;
                    case 35:
                        description.setText("Panatilihing malinis ang taniman, tanggalin ang mga ligaw na halaman.\n" +
                                "Ibahin ang pagtataniman ng halaman kada taon.\n" +
                                "Maaaring gamitan ng copper-based fungicides.\n");
                        break;
                    default:
                        break;
                }
            }else if (maxPos==17){
                result.setText("Hindi ito halaman.");
            }else{
                String halaman ="";
                switch(plantstr){
                    case "banana":
                        halaman = "saging.";
                        break;
                    case "cabbage":
                        halaman = "pechay.";
                        break;
                    case "coffee":
                        halaman = "kape.";
                        break;
                    case "corn":
                        halaman = "mais.";
                        break;
                    case "cotton":
                        halaman = "bulak.";
                        break;
                    case "rice":
                        halaman = "palay.";
                        break;
                    case "sugarcane":
                        halaman = "tubo.";
                        break;
                    case "tomato":
                        halaman = "kamatis.";
                        break;

                }
                result.setText("Hindi ito isang "+ halaman);
            }*/

            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }

    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,@Nullable Intent data){

        if(resultCode == RESULT_OK){
            if(requestCode == 3){
                result.setText("");
                description.setText("");
                Bitmap image = (Bitmap) data.getExtras().get("data");
                int dimension = Math.min(image.getWidth(), image.getHeight());
                image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
                imageView.setImageBitmap(image);
                image = Bitmap.createScaledBitmap(image,imageSize,imageSize,true);
                classifyImage(image, str);

            }else{
                result.setText("");
                description.setText("");
                Uri dat = data.getData();
                Bitmap image = null;
                try{
                    image = MediaStore.Images.Media.getBitmap(this.getContentResolver(),dat);
                }catch(IOException e){
                    e.printStackTrace();
                }
                imageView.setImageBitmap(image);
                image = Bitmap.createScaledBitmap(image,imageSize,imageSize,true);
                classifyImage(image, str);

            }
        }
        super.onActivityResult(requestCode,resultCode, data);
    }

}