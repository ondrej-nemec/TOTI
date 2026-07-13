package toti.application.answers.session;

import java.util.Map;
import java.util.Optional;

import toti.lib.common.structures.MapDictionary;

public record CurrentSession(String sessionId, Map<String, MapDictionary<String>> sessionSpace, Optional<Object> user) {

}
