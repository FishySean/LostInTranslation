package translation;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class GUI {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // ① 准备好翻译器和两个转换器
            Translator translator = new JSONTranslator();
            LanguageCodeConverter languageConverter = new LanguageCodeConverter();
            CountryCodeConverter countryConverter = new CountryCodeConverter();

            // ② 第一行：语言下拉菜单
            JPanel languagePanel = new JPanel();
            languagePanel.add(new JLabel("Language:"));
            JComboBox<String> languageComboBox = new JComboBox<>();
            for (String code : translator.getLanguageCodes()) {
                languageComboBox.addItem(languageConverter.fromLanguageCode(code));
            }
            languagePanel.add(languageComboBox);

            // ③ 第二行：显示翻译结果
            JPanel resultPanel = new JPanel();
            resultPanel.add(new JLabel("Translation:"));
            JLabel resultLabel = new JLabel(" ");
            resultPanel.add(resultLabel);

            // ④ 第三块：可滚动的国家列表
            List<String> countryCodes = translator.getCountryCodes();
            String[] countryNames = new String[countryCodes.size()];
            for (int i = 0; i < countryCodes.size(); i++) {
                countryNames[i] = countryConverter.fromCountryCode(countryCodes.get(i));
            }
            JList<String> countryList = new JList<>(countryNames);
            countryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            JScrollPane scrollPane = new JScrollPane(countryList);

            // ⑤ 监听器：换了语言或者换了国家，都重新翻译一次
            languageComboBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    updateTranslation(languageComboBox, countryList, resultLabel,
                        translator, languageConverter, countryConverter);
                }
            });
            countryList.addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    updateTranslation(languageComboBox, countryList, resultLabel,
                        translator, languageConverter, countryConverter);
                }
            });

            // ⑥ 大柜子竖着排，放进窗口
            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.add(languagePanel);
            mainPanel.add(resultPanel);
            mainPanel.add(scrollPane);

            JFrame frame = new JFrame("Country Name Translator");
            frame.setContentPane(mainPanel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        });
    }

    /**
     * Read the current selections, translate, and show the result.
     */
    private static void updateTranslation(JComboBox<String> languageComboBox,
                                          JList<String> countryList,
                                          JLabel resultLabel,
                                          Translator translator,
                                          LanguageCodeConverter languageConverter,
                                          CountryCodeConverter countryConverter) {
        String languageName = (String) languageComboBox.getSelectedItem();
        String countryName = countryList.getSelectedValue();
        if (languageName == null || countryName == null) {
            return;   // 还没选完，先不翻译
        }
        String languageCode = languageConverter.fromLanguage(languageName);
        String countryCode = countryConverter.fromCountry(countryName);
        String result = translator.translate(countryCode, languageCode);
        if (result == null) {
            result = "no translation found!";
        }
        resultLabel.setText(result);
    }
}