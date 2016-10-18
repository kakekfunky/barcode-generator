package sarikode.com.barcodegenerator;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.EnumMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    /** inisialisasi objek di layout */
    EditText textInput;
    Button tombolGenerate;
    ImageView gambarBarcode;
    TextView textnya;
    Bitmap bitmap = null;

    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /** hubungkan objek di layout ke variabelnya */
        textInput = (EditText) findViewById(R.id.input_isi);
        tombolGenerate = (Button) findViewById(R.id.btn_generate);
        gambarBarcode = (ImageView) findViewById(R.id.img_barcode);
        textnya = (TextView) findViewById(R.id.txt_hasil);

        /** buat gambar barcode nya ketika tombol di tekan*/
        tombolGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    bitmap = encodeAsBitmap(textInput.getText().toString(), BarcodeFormat.CODE_128, 600, 300);

                    gambarBarcode.setImageBitmap(bitmap);
                    textnya.setText(textInput.getText().toString());
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int img_width, int img_height) throws WriterException {
        String contentsToEncode = contents;
        if (contentsToEncode == null) {
            return null;
        }
        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(contentsToEncode);
        if (encoding != null) {
            hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result;
        try {
            result = writer.encode(contentsToEncode, format, img_width, img_height, hints);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private static String guessAppropriateEncoding(CharSequence contents) {
        // Very crude at the moment
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }
}
