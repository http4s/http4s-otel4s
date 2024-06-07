/*
 * Copyright 2023 http4s.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.http4s.otel4s.middleware.trace
package client

import cats.effect.IO
import org.http4s.HttpApp
import org.http4s.Request
import org.http4s.client.Client
import org.typelevel.otel4s.sdk.trace.data.SpanData
import org.typelevel.otel4s.trace.SpanKind
import org.typelevel.otel4s.trace.Tracer

class ClientMiddlewareTest extends MiddlewareTest[ClientMiddleware.Builder, Client] {
  protected def middlewareBuilder(implicit tracer: Tracer[IO]): ClientMiddleware.Builder[IO] =
    ClientMiddleware.builder
  protected def build(builder: ClientMiddleware.Builder[IO], app: HttpApp[IO]): Client[IO] =
    builder.build(Client.fromHttpApp(app))
  protected def runRequest(traced: Client[IO], request: Request[IO]): IO[Unit] =
    traced.run(request).use(_.body.compile.drain)
  protected def checkSpanNameAndKind(span: SpanData): Unit = {
    assertEquals(span.name, "Http Client - GET")
    assertEquals(span.kind, SpanKind.Client)
  }

  testMiddleware("ClientMiddleware")
}
