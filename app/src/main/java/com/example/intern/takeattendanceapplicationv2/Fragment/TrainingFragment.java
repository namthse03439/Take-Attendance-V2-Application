package com.example.intern.takeattendanceapplicationv2.Fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.intern.takeattendanceapplicationv2.BaseClass.GlobalVariable;
import com.example.intern.takeattendanceapplicationv2.BaseClass.ServiceGenerator;
import com.example.intern.takeattendanceapplicationv2.BaseClass.StringClient;
import com.example.intern.takeattendanceapplicationv2.LogInActivity;
import com.example.intern.takeattendanceapplicationv2.MainActivity;
import com.example.intern.takeattendanceapplicationv2.R;
import com.facepp.http.HttpRequests;
import com.facepp.http.PostParameters;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TrainingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TrainingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TrainingFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final int REQUEST_TAKE_PHOTO = 1;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private int count = 0;
    Activity context;

    private OnFragmentInteractionListener mListener;

    public TrainingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TrainingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TrainingFragment newInstance(String param1, String param2) {
        TrainingFragment fragment = new TrainingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        context = this.getActivity();
        dispatchTakePictureIntent();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_training, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);

        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpeg",         /* suffix */
                storageDir      /* directory */
        );
//        System.out.println("check");

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == context.RESULT_OK) {
            final ProgressDialog progressDialog = new ProgressDialog(context, R.style.AppTheme);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Image is sending to server...");
            progressDialog.show();

            // =============================

            resizeImage(mCurrentPhotoPath);
            trainingFunction();

            // -----------------------------

            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            // On complete call either onLoginSuccess or onLoginFailed
                            //onLoginSuccess();
                            // onLoginFailed();
                            progressDialog.dismiss();
                        }
                    }, 3000);
        }

    }

    void resizeImage(String mCurrentPhotoPath){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, options);

        int oldWidth = bitmap.getWidth();
        int oldHeight = bitmap.getHeight();

        double ratio = Math.sqrt(GlobalVariable.imageArea / (oldHeight * oldWidth));

        int newWidth = (int) (oldWidth * ratio);
        int newHeight = (int) (oldHeight * ratio);
        Bitmap resized = bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);

        File file = new File(mCurrentPhotoPath);
        if(file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            resized.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    void trainingFunction() {
        Context context = this.context;
        TrainThread trainThread = new TrainThread(mCurrentPhotoPath, context);
        trainThread.start();
    }


}

//============= test code ================



//TODO: remember to resize first
class VerifyThread extends Thread{
    Thread t;
    String mCurrentPhotoPath = null;
    Context context;
    public VerifyThread(String _mCurrentPhotoPath, Context _context){
        mCurrentPhotoPath = _mCurrentPhotoPath;
        context = _context;
    }

    public void run(){
        HttpRequests httpRequests = new HttpRequests(GlobalVariable.apiKey, GlobalVariable.apiSecret);
        File imgFile = new File(mCurrentPhotoPath);

        SharedPreferences pref = context.getSharedPreferences("ATK_pref", 0);
        String auCode = pref.getString("authorizationCode", null);

        String personID = getThisPersonID(auCode);
        if(personID.compareTo("") != 0){
            String faceID = get1FaceID(httpRequests, imgFile);
            double result = getVerification(httpRequests, personID, faceID);

            //TODO: send this verifyResult to local server and get response, notify user about the response
        }
        else{
            //TODO: Show notification to user: you must train first, before verirying
        }

    }

    private double getVerification(HttpRequests httpRequests, String personID, String faceID) {
        double result = 0;
        PostParameters postParameters = new PostParameters().setPersonId(personID).setFaceId(faceID);
        try{
            JSONObject fppResult = httpRequests.recognitionVerify(postParameters);
            result = fppResult.getDouble("confidence");
            if(!fppResult.getBoolean("is_same_person"))
                result = 100 - result;
        }
        catch(Exception e){
            System.out.print("Process interrupted!");
        }

        return result;
    }


    String getThisPersonID(String auCode){

        String personID = null;

        StringClient client = ServiceGenerator.createService(StringClient.class, auCode);
        Call<ResponseBody> call = client.getPersonID();

        try {
            Response<ResponseBody> response = call.execute();
            JSONObject data = new JSONObject(response.body().string());
            personID = data.getString("person_id");
        }
        catch(Exception e){
            System.out.print("Error get this personID");
        }

        return personID;
    }

    String get1FaceID(HttpRequests httpRequests, File imgFile){
        String faceID = null;
        try {
            PostParameters postParameters = new PostParameters().setImg(imgFile).setMode("oneface");
            JSONObject faceResult = httpRequests.detectionDetect(postParameters);
            faceID = faceResult.getJSONArray("face").getJSONObject(0).getString("face_id");
        }
        catch(Exception e){
            System.out.print("Get FaceID failed!");
        }
        return faceID;
    }
}

//========================================


class TrainThread extends Thread{
    Thread t;
    String mCurrentPhotoPath = null;
    Context context;

    public TrainThread(String _mCurrentPhotoPath, Context _context){
        mCurrentPhotoPath = _mCurrentPhotoPath;
//        mCurrentPhotoPath = "Removable/MicroSD/corel1000/minority/1.jpg";
//        mCurrentPhotoPath = "/sdcard/Pictures/resized.jpg";
        context = _context;
    }

    public void run(){
        HttpRequests httpRequests = new HttpRequests(GlobalVariable.apiKey, GlobalVariable.apiSecret);
        File imgFile = new File(mCurrentPhotoPath);

        SharedPreferences pref = context.getSharedPreferences("ATK_pref", 0);
        String auCode = pref.getString("authorizationCode", null);

        String newFaceID = get1FaceID(httpRequests, imgFile);
        String personID = getThisPersonID(auCode);

        if(personID.compareTo("") != 0){ //this person has been trained before

            ArrayList faceIDList = getThisFaceIDList(auCode);
            faceIDList = substitute1FacefromPerson(httpRequests, personID, faceIDList, newFaceID);
            postFaceIDListtoLocalServer(auCode, faceIDList);
        }
        else{
            personID = create1Person(httpRequests, newFaceID);
            postPersonIDtoLocalServer(auCode, personID);
            ArrayList<String> faceIDList = new ArrayList<String>();
            faceIDList.add(newFaceID);
            postFaceIDListtoLocalServer(auCode, faceIDList);
        }
        //TODO: Show notification about sucessful training
        System.out.print("Trained OK!");
    }

    void postPersonIDtoLocalServer(String auCode, String personID){
        StringClient client = ServiceGenerator.createService(StringClient.class, auCode);
        Call<ResponseBody> call = client.postPersonID(personID);

        try{
            Response<ResponseBody> response = call.execute();
            int messageCode = response.code();
        }
        catch(Exception e){
            System.out.print("Error get this faceID list");
        }

    }

    String create1Person(HttpRequests httpRequests, String faceID){
        String personID = null;

        try {
            PostParameters postParameters = new PostParameters().setFaceId(faceID);
            JSONObject person = httpRequests.personCreate(postParameters);
            personID = person.getString("person_id");

            postParameters = new PostParameters().setPersonId(personID);
            httpRequests.trainVerify(postParameters);
        }
        catch (Exception e){
            System.out.print("Caught!");
        }

        return personID;
    }

    void postFaceIDListtoLocalServer(String auCode, ArrayList<String> faceIDList){
        try {
            StringClient client = ServiceGenerator.createService(StringClient.class, auCode);
            Call<ResponseBody> call = client.postFaceIDList(faceIDList);
            Response<ResponseBody> response = call.execute();
            int messageCode = response.code();
            if(messageCode != 200) {
                //TODO: show error
            }
        }
        catch(Exception e){
            System.out.print("post faceIDList to server exception!");
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    ArrayList substitute1FacefromPerson(HttpRequests httpRequests, String personID, ArrayList faceIDList, String newFaceID){

        try{

            // get the earliest faceID in the list, that is the faceID with index 0
            if(faceIDList != null && faceIDList.size() == GlobalVariable.maxLengthFaceList) {
                String oldFaceID = faceIDList.get(0).toString();
                // remove it on Face++
                PostParameters postParameters = new PostParameters().setPersonId(personID).setFaceId(oldFaceID);
                httpRequests.personRemoveFace(postParameters);
                // remove it on the list
                faceIDList.remove(0);
            }

            // add 1 face on Face++
            PostParameters postParameters = new PostParameters().setPersonId(personID).setFaceId(newFaceID);
            httpRequests.personAddFace(postParameters);
            // add 1 face on the list
            if(faceIDList != null)
                faceIDList.add(newFaceID);
            else {
                faceIDList = new ArrayList();
                faceIDList.add(newFaceID);
            }

            //re-train person on Face++
            postParameters = new PostParameters().setPersonId(personID);
            httpRequests.trainVerify(postParameters);

            return faceIDList;

        }
        catch(Exception e){
            System.out.print("Caught!");
        }

        return null;
    }

    ArrayList getThisFaceIDList(String auCode){

        ArrayList<String> result = null;

        StringClient client = ServiceGenerator.createService(StringClient.class, auCode);
        Call<ResponseBody> call = client.getFaceIDList();

        try{
            Response<ResponseBody> response = call.execute();
            JSONObject data = new JSONObject(response.body().string());
            JSONArray arr = data.getJSONArray("face_id");

            result = new ArrayList<String>();
            for (int i = 0; i < arr.length(); i++)
                result.add(arr.getString(i).toString());

        }
        catch(Exception e){
            System.out.print("Error get this faceID list");
        }

        return result;

    }

    String getThisPersonID(String auCode){

        String personID = null;

        StringClient client = ServiceGenerator.createService(StringClient.class, auCode);
        Call<ResponseBody> call = client.getPersonID();

        try {
            Response<ResponseBody> response = call.execute();
            JSONObject data = new JSONObject(response.body().string());
            personID = data.getString("person_id");
        }
        catch(Exception e){
            System.out.print("Error get this personID");
        }

        return personID;
    }

    String get1FaceID(HttpRequests httpRequests, File imgFile){
        String faceID = null;
        try {
            PostParameters postParameters = new PostParameters().setImg(imgFile).setMode("oneface");
            JSONObject faceResult = httpRequests.detectionDetect(postParameters);
            faceID = faceResult.getJSONArray("face").getJSONObject(0).getString("face_id");
        }
        catch(Exception e){
            System.out.print("Get FaceID failed!");
        }
        return faceID;
    }
}

