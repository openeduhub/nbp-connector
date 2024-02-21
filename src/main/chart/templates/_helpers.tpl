{{- define "edu_sharing_projects_wlo_nbp_connector.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{- define "edu_sharing_projects_wlo_nbp_connector.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{- define "edu_sharing_projects_wlo_nbp_connector.labels" -}}
{{ include "edu_sharing_projects_wlo_nbp_connector.labels.instance" . }}
helm.sh/chart: {{ include "edu_sharing_projects_wlo_nbp_connector.chart" . }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end -}}

{{- define "edu_sharing_projects_wlo_nbp_connector.labels.instance" -}}
{{ include "edu_sharing_projects_wlo_nbp_connector.labels.app" . }}
{{ include "edu_sharing_projects_wlo_nbp_connector.labels.version" . }}
{{- end -}}

{{- define "edu_sharing_projects_wlo_nbp_connector.labels.version" -}}
version: {{ .Chart.AppVersion | quote }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end -}}

{{- define "edu_sharing_projects_wlo_nbp_connector.labels.app" -}}
app: {{ include "edu_sharing_projects_wlo_nbp_connector.name" . }}
app.kubernetes.io/name: {{ include "edu_sharing_projects_wlo_nbp_connector.name" . }}
{{- end -}}

{{- define "edu_sharing_projects_wlo_nbp_connector.image" -}}
{{- $registry := default .Values.global.image.registry .Values.image.registry -}}
{{- $repository := default .Values.global.image.repository .Values.image.repository -}}
{{ $registry }}{{ if $registry }}/{{ end }}{{ $repository }}{{ if $repository }}/{{ end }}
{{- end -}}
