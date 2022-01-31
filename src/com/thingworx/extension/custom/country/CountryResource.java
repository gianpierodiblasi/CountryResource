package com.thingworx.extension.custom.country;

import com.thingworx.data.util.InfoTableInstanceFactory;
import com.thingworx.entities.utils.EntityUtilities;
import com.thingworx.logging.LogUtilities;
import com.thingworx.metadata.annotations.ThingworxServiceDefinition;
import com.thingworx.metadata.annotations.ThingworxServiceParameter;
import com.thingworx.metadata.annotations.ThingworxServiceResult;
import com.thingworx.relationships.RelationshipTypes.ThingworxRelationshipTypes;
import com.thingworx.resources.Resource;
import com.thingworx.resources.sessioninfo.SessionInfo;
import com.thingworx.types.InfoTable;
import com.thingworx.types.collections.ValueCollection;
import com.thingworx.types.data.sorters.GenericSorter;
import com.thingworx.types.primitives.StringPrimitive;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;

public class CountryResource extends Resource {

  private final static Logger SCRIPT_LOGGER = LogUtilities.getInstance().getScriptLogger(CountryResource.class);
  private final static Set<String> ISO = new TreeSet<>(Arrays.asList(Locale.getISOCountries()));
  private final static Map<String, List<Locale>> LOCALES = Stream.of(Locale.getAvailableLocales()).collect(Collectors.groupingBy(Locale::getCountry));
  private static final long serialVersionUID = 1L;

  @ThingworxServiceDefinition(name = "getCountries", description = "", category = "", isAllowOverride = false, aspects = {"isAsync:false"})
  @ThingworxServiceResult(name = "result", description = "", baseType = "INFOTABLE", aspects = {"isEntityDataShape:true", "dataShape:ds_Country"})
  public InfoTable getCountries(@ThingworxServiceParameter(name = "language", description = "", baseType = "STRING") String language, @ThingworxServiceParameter(name = "sortByName", description = "", baseType = "BOOLEAN") Boolean sortByName) throws Exception {
    Locale locale = new Locale(language != null && !language.isEmpty() ? language.toLowerCase() : this.getUserLanguage());
    List<Locale> localeLanguage = List.of(locale);

    InfoTable table = InfoTableInstanceFactory.createInfoTableFromDataShape("ds_Country");
    ISO.forEach(isoCountry -> this.addRow(table, isoCountry, localeLanguage, locale));
    this.sortByName(table, sortByName);
    return table;
  }

  @ThingworxServiceDefinition(name = "getCountriesInOwnLanguage", description = "", category = "", isAllowOverride = false, aspects = {"isAsync:false"})
  @ThingworxServiceResult(name = "result", description = "", baseType = "INFOTABLE", aspects = {"isEntityDataShape:true", "dataShape:ds_Country"})
  public InfoTable getCountriesInOwnLanguage(@ThingworxServiceParameter(name = "sortByName", description = "", baseType = "BOOLEAN") Boolean sortByName) throws Exception {
    List<Locale> localeDefaultLanguage = List.of(new Locale(this.getUserLanguage()));

    InfoTable table = InfoTableInstanceFactory.createInfoTableFromDataShape("ds_Country");
    ISO.forEach(isoCountry -> this.addRow(table, isoCountry, LOCALES.getOrDefault(isoCountry, localeDefaultLanguage), null));
    this.sortByName(table, sortByName);
    return table;
  }

  private String getUserLanguage() throws Exception {
    SessionInfo currentSessionInfo = (SessionInfo) EntityUtilities.findEntity("CurrentSessionInfo", ThingworxRelationshipTypes.Resource);
    String language = currentSessionInfo.GetCurrentUserLanguage();
    return language == null || language.isEmpty() || "Default".equalsIgnoreCase(language) || "System".equalsIgnoreCase(language) ? "en" : language.substring(0, 2).toLowerCase();
  }

  private void addRow(InfoTable table, String isoCountry, List<Locale> localeLanguages, Locale langForLanguages) {
    Locale locale = new Locale("", isoCountry);
    StringBuilder name = new StringBuilder();
    localeLanguages.forEach(localeLanguage -> {
      String displayCountry = locale.getDisplayCountry(localeLanguage);
      if (name.indexOf(displayCountry) == -1) {
        name.append(name.length() == 0 ? "" : ", ").append(displayCountry);
      }
    });

    StringBuilder language = new StringBuilder();
    if (LOCALES.containsKey(isoCountry)) {
      LOCALES.get(isoCountry).forEach(localeLanguage -> {
        String displayLanguage = localeLanguage.getDisplayLanguage(langForLanguages != null ? langForLanguages : localeLanguage);
        if (language.indexOf(displayLanguage) == -1) {
          language.append(language.length() == 0 ? "" : ", ").append(displayLanguage);
        }
      });
    }

    ValueCollection values = new ValueCollection();
    values.put("iso", new StringPrimitive(isoCountry));
    values.put("iso3", new StringPrimitive(locale.getISO3Country()));
    values.put("name", new StringPrimitive(name.toString()));
    values.put("language", new StringPrimitive(language.toString()));
    table.addRow(values);
  }

  private void sortByName(InfoTable table, Boolean sortByName) {
    if (sortByName != null && sortByName) {
      table.sortRows(new GenericSorter("name", true));
    }
  }
}
