package gui.adapter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class GenericAdapterData {

    enum ItemTypes {
        ItemType_Text, ItemType_Image, ItemType_Button,
    }

    public interface GenericItem {
        void fillItem(View view, int id);
    }

    class TextItem implements GenericItem {

        String text;
        TextView textView;

        public TextItem(String text) {
            this.text = text;
        }

        public void fillItem(View view, int id) {
            // TODO Auto-generated method stub

            textView = (TextView) view.findViewById(id);
            textView.setText(text);

        }

        public String getCurrentText() {
            return textView.getText().toString();
        }

    }

    class ImageItem implements GenericItem {

        int imageId;

        public ImageItem(int imageId) {
            this.imageId = imageId;
        }

        public void fillItem(View view, int id) {
            // TODO Auto-generated method stub
            ImageView imageView = (ImageView) view.findViewById(id);
            imageView.setImageResource(imageId);
            imageView.invalidate();
        }

    }

    class ButtonItem implements GenericItem {

        View.OnClickListener onClickListener;

        public ButtonItem(View.OnClickListener onClickListener) {
            this.onClickListener = onClickListener;
        }

        public void fillItem(View view, int id) {
            // TODO Auto-generated method stub
            Button button = (Button) view.findViewById(id);
            button.setOnClickListener(onClickListener);

        }

    }

    GenericItemDescription description;
    Map<String, GenericItem> items = new HashMap<String, GenericItem>();

    public GenericAdapterData(GenericItemDescription desc) {
        description = desc;
    }

    public void fillView(View view) {
        Iterator<Entry<String, GenericItem>> iterator = items.entrySet()
                .iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, GenericItem> pairs = iterator.next();
            pairs.getValue().fillItem(view,
                    description.getResourceId(pairs.getKey()));
        }

    }

    public void SetText(String tag, String text) {
        items.put(tag, new TextItem(text));
    }

    public void SetImage(String tag, int image) {
        items.put(tag, new ImageItem(image));
    }

    public void SetButtonCallback(String tag,
            View.OnClickListener onClickListener) {
        items.put(tag, new ButtonItem(onClickListener));

    }

    public String getText(String tag) {
        TextItem textItem = (TextItem) items.get(tag);
        return textItem.getCurrentText();

    }

}
