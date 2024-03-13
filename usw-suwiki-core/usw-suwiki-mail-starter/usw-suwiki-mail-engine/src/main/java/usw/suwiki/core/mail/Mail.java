package usw.suwiki.core.mail;

import org.thymeleaf.context.Context;

record Mail(
  String to,
  String template,
  Context context
) {
}
