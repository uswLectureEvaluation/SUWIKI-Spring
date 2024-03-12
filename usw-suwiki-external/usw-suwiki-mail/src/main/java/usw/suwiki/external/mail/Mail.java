package usw.suwiki.external.mail;

import org.thymeleaf.context.Context;

record Mail(
  String to,
  String template,
  Context context
) {
}
